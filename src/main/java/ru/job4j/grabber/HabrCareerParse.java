package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String
            .format("%s/vacancies/java_developer?page=", SOURCE_LINK);
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) throws IOException {
       for (int i = 1; i <= 5; i++) {
           Connection connection = Jsoup.connect(String.format("%s%d", PAGE_LINK, i));
           Document document = connection.get();
           Elements rows = document.select(".vacancy-card__inner");
           rows.forEach(row -> {
               Element titleElement = row.select(".vacancy-card__title").first();
               Element linkElement = titleElement.child(0);
               Element dateTime = row.select(".vacancy-card__date").first();
               String date = dateTime.child(0).attr("datetime");
               String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
               System.out.printf("%s %s %s%n", titleElement.text(), link, date);
           });
       }
        System.out.println("конец цикла");

    }

    private String retrieveDescription(String url) throws IOException {
        Connection connection = Jsoup.connect(url);
        Document document = connection.get();
        Element element = document.select(".style-ugc").first();
        return element.text();
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> postList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Connection connection = Jsoup.connect(String.format("%s%d", PAGE_LINK, i));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                String rowLink = String.format("%s%s", SOURCE_LINK,
                        titleElement.child(0).attr("href"));
                String description = "";
                try {
                    description = retrieveDescription(rowLink);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                LocalDateTime dateTime = dateTimeParser.parse(row.select(".vacancy-card__date")
                        .first().child(0).attr("datetime"));
                postList.add(new Post(titleElement.text(), rowLink, description, dateTime));
            });
        }
        return postList;
    }
}