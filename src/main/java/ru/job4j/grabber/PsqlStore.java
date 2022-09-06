package ru.job4j.grabber;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
            Connection cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
        } catch (Exception ex) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement(
                "insert into post(name, description, link, created) values (?, ?, ?, ?)")) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
        } catch (SQLException sqlException) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> postList = new ArrayList<>();
        try (Statement statement = cnn.createStatement()) {
            ResultSet result = statement.executeQuery("select * from post");
            while (result.next()) {
                postList.add(new Post(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("description"),
                        result.getString("link"),
                        result.getTimestamp("created").toLocalDateTime()
                ));
            }
        } catch (SQLException sqlException) {
            throw new IllegalArgumentException();
        }
        return postList;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement = cnn.prepareStatement(
                "select * from post where id = ?")) {
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                post = new Post(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("description"),
                        result.getString("link"),
                        result.getTimestamp("created").toLocalDateTime()
                );
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}
