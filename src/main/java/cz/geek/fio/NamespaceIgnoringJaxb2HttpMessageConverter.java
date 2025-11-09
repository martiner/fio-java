package cz.geek.fio;

import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

/**
 * Players in FIO publish schema with namespace, but XML from API doesn't contain them :(
 */
class NamespaceIgnoringJaxb2HttpMessageConverter extends Jaxb2RootElementHttpMessageConverter {

    @Override
    protected Source processSource(Source source) {
        if (source instanceof StreamSource streamSource) {
            InputSource inputSource = new InputSource(streamSource.getInputStream());
            try {
                XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
                String featureName = "http://xml.org/sax/features/external-general-entities";
                xmlReader.setFeature(featureName, isProcessExternalEntities());
                if (!isProcessExternalEntities()) {
                    xmlReader.setEntityResolver(NO_OP_ENTITY_RESOLVER);
                }

                NamespaceFilter inFilter = new NamespaceFilter("http://www.fio.cz/IBSchema", true);
                inFilter.setParent(xmlReader);

                return new SAXSource(inFilter, inputSource);
            }
            catch (SAXException ex) {
                logger.warn("Processing of external entities could not be disabled", ex);
                return source;
            } catch (ParserConfigurationException e) {
                throw new RuntimeException("Unable to create parser", e);
            }
        }
        throw new IllegalArgumentException("Source must be of type StreamSource not " + source.getClass().getName());
    }

    private static final EntityResolver NO_OP_ENTITY_RESOLVER = new EntityResolver() {
        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
            return new InputSource(new StringReader(""));
        }
    };

}
