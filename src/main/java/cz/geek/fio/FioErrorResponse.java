package cz.geek.fio;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

/**
 * Maps the structured error body Fio returns on non-2xx responses, e.g.
 * {@code <response><result><errorCode>21</errorCode><status>error</status><message>...</message></result></response>}.
 * Namespace is empty everywhere: the generated package-info defaults this package to the qualified IBSchema
 * namespace, but the error body has none.
 */
@XmlRootElement(name = "response", namespace = "")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
class FioErrorResponse {

    @XmlElement(namespace = "")
    private Result result;

    @XmlAccessorType(XmlAccessType.FIELD)
    @Getter
    static class Result {

        @XmlElement(namespace = "")
        private Integer errorCode;

        @XmlElement(namespace = "")
        private String status;

        @XmlElement(namespace = "")
        private String message;
    }

}
