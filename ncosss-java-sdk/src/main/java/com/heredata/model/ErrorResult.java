package com.heredata.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Error")
public class ErrorResult {

    @XmlElement(name = "Code")
    public String Code;

    @XmlElement(name = "Message")
    public String Message;

    @XmlElement(name = "RequestId")
    public String RequestId;

    @XmlElement(name = "HostId")
    public String HostId;

    @XmlElement(name = "ResourceType")
    public String ResourceType;

    @XmlElement(name = "Method")
    public String Method;

    @XmlElement(name = "Header")
    public String Header;

}
