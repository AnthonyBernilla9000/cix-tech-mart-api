package org.istrfa.utils;

import lombok.extern.slf4j.Slf4j;
import org.istrfa.Main;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class UtilHtml {

    public static Document converHtmltoDocument(String htmlFilePath) throws IOException {
        String filestring= new String(Objects.requireNonNull(Main.class
                        .getResourceAsStream(htmlFilePath))
                .readAllBytes());
        return Jsoup.parse(filestring);
    }

}
