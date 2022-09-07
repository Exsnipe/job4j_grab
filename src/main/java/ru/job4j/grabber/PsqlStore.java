package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private final Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
            cnn = DriverManager.getConnection(
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
                "insert into post(name, description, link, created)"
                        + " values (?, ?, ?, ?) on conflict(link) do nothing")) {
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
                postList.add(getPostFromResultSet(result));
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
                post = getPostFromResultSet(result);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return post;
    }

    private Post getPostFromResultSet(ResultSet set) throws SQLException {
        return new Post(
                set.getInt("id"),
                set.getString("name"),
                set.getString("link"),
                set.getString("description"),
                set.getTimestamp("created").toLocalDateTime()
        );
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) {
        Properties cfg = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().
                getResourceAsStream("rabbit.properties")) {
            cfg.load(in);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        try (PsqlStore store = new PsqlStore(cfg)) {
            store.save(new Post("1", "2", "3", LocalDateTime.now()));
            List<Post> postList = store.getAll();
            System.out.println(postList.size());
            Post post = store.findById(6);
            System.out.println(post.getDescription());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
