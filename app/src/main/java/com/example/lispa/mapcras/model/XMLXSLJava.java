package com.example.lispa.mapcras.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XMLXSLJava {
    public static void main(String[] args) throws IOException, URISyntaxException, TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();

        Source xslt = new StreamSource(new File(args[0]));
        Transformer transformer = factory.newTransformer(xslt);

        Source text = new StreamSource(new File(args[1]));
        transformer.transform(text, new StreamResult(new File(args[2])));
    }
}