package ru.yakovlev.businesscalendar.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import ru.yakovlev.businesscalendar.service.DaysOffService;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DaysOffServiceImpl implements DaysOffService {
    private final RestTemplate rest;

    public DaysOffServiceImpl(RestTemplateBuilder builder) {
        rest = builder
                .rootUri("https://xmlcalendar.ru/data/ru")
                .build();
    }

    @Override
    public Map<LocalDate, Integer> getDaysOff(Integer year) {
        String url = "/" + year + "/calendar.xml";

        String xml = rest.getForEntity(url, String.class).getBody();
        Map<LocalDate, Integer> daysOff = new HashMap<>();

        try (Reader reader = new StringReader(xml)) {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(new InputSource(reader));

            NodeList list = doc.getDocumentElement().getElementsByTagName("day");

            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                LocalDate date = makeLocalDate(year, element.getAttribute("d"));
                daysOff.put(date, Integer.parseInt(element.getAttribute("t")));
            }
        } catch (Exception ex) {
            log.error("Xml parsing error, xml: {}.", xml, ex);
        }
        return daysOff;
    }

    private LocalDate makeLocalDate(Integer year, String date) {
        String[] split = date.split("\\.");
        return LocalDate.of(year, Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
}
