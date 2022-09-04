package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String
            .format("%s/vacancies/java_developer?page=", SOURCE_LINK);
    private final static  int LOOP_NUMBER = 5;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private String retrieveDescription(String url) {
        Connection connection = Jsoup.connect(url);
        try {
            Document document = connection.get();
            Element element = document.select(".style-ugc").first();
            return element.text();
        } catch (IOException ex) {
            throw new IllegalArgumentException();
        }
    }

    private Post parseElements(Element row) {
        Element titleElement = row.select(".vacancy-card__title").first();
        String rowLink = String.format("%s%s", SOURCE_LINK,
                titleElement.child(0).attr("href"));
        String description = retrieveDescription(rowLink);
        LocalDateTime dateTime = dateTimeParser.parse(row.select(".vacancy-card__date")
                .first().child(0).attr("datetime"));
        return new Post(titleElement.text(), rowLink, description, dateTime);
    }

    @Override
    public List<Post> list(String link) {
        List<Post> postList = new ArrayList<>();
        for (int i = 1; i <= LOOP_NUMBER; i++) {
            Connection connection = Jsoup.connect(String.format("%s%d", link, i));
            try {
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    postList.add(parseElements(row));
                });
            } catch (IOException ioe) {
                throw new IllegalArgumentException();
            }
        }
        return postList;
    }
}