package ge.edu.freeuni.android.entertrainment.server.services.music.DO;


import ge.edu.freeuni.android.entertrainment.server.services.music.data.Music;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MusicDo {

    private static Connection getConnection() {
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        try {
            if(dbUrl == null){
                Class.forName("org.postgresql.Driver");
                return DriverManager
                        .getConnection("jdbc:postgresql://localhost:5432/music",
                                "postgres", "123");
            }
            return DriverManager.getConnection(dbUrl);
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }


        return null;
    }




    public static void createDatabase() throws SQLException {
        Connection connection =getConnection();
        Statement
                stmt = connection != null ? connection.createStatement() : null;
        String createMusicTable = " CREATE TABLE IF NOT EXISTS MUSICS  " +
                "(ID TEXT    PRIMARY KEY      NOT NULL," +
                "MUSIC_NAME    TEXT             NOT NULL, " +
                "RATING        INT              NOT NULL, " +
                "IMAGE_PATH    TEXT," +
                "MUSIC_DATA    BYTEA           NOT NULL ," +
                "DURATION   INT NOT NULL )";

        String createVotesTable = "CREATE TABLE IF NOT EXISTS VOTES  " +
                "(ID SERIAL     PRIMARY KEY     NOT NULL," +
                "MUSIC_ID       TEXT             NOT NULL REFERENCES MUSICS(ID), " +
                "VOTE           TEXT            NOT NULL," +
                "IP             TEXT            NOT NULL )";


        if (stmt != null) {
            stmt.executeUpdate(createMusicTable);
            stmt.executeUpdate(createVotesTable);
        }
        if (connection != null) {
            connection.close();
        }


    }

    public static Music getMusic(String musicId){
        Connection connection = getConnection();
        try {
            if (connection != null) {
                Statement statement = connection.createStatement();
                String query = "SELECT ID, MUSIC_NAME, RATING, IMAGE_PATH, DURATION FROM MUSICS WHERE ID = '"+musicId+"'";
                ResultSet resultSet = statement.executeQuery(query);
                if ( resultSet.next() ) {
                    String id = resultSet.getString("ID");
                    String  name = resultSet.getString("MUSIC_NAME");
                    int rating = resultSet.getInt("RATING");
                    int duration = resultSet.getInt("DURATION");
                    String imagePath = resultSet.getString("IMAGE_PATH");
                    connection.close();
                    return new Music(duration,id,name,rating,imagePath);
                }
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }



    public static byte[] getMusicData(String musicId){
        Connection connection = getConnection();
        try {
            if (connection != null) {
                Statement statement = connection.createStatement();
                String query = "SELECT MUSICS.MUSIC_DATA FROM MUSICS  WHERE MUSICS.ID = '"+musicId+"'";
                ResultSet resultSet = statement.executeQuery(query);
                if ( resultSet.next() ) {
                    byte[] res = resultSet.getBytes("MUSIC_DATA");
                    System.out.println(res.length);
                    connection.close();
                    return res;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveMusic(Music music, byte[] bytes){
        Connection connection = getConnection();
        try {
            if (connection != null) {
                String sql = "INSERT INTO MUSICS (MUSIC_NAME,IMAGE_PATH,RATING, MUSIC_DATA, ID,DURATION) "
                        + "VALUES (?,?,?,?,?,?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1,music.getName());
                statement.setString(2,music.getImagePath());
                statement.setInt(3,music.getRating());
                statement.setBytes(4,bytes);
                statement.setString(5,music.getId());
                statement.setInt(6,music.getDuration());
                statement.executeUpdate();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void vote(String musicId, int dif, String vote, String ip){
        Connection connection =  getConnection();

        try {
            if (connection != null) {
                Statement statement = connection.createStatement();
                String sql = "UPDATE MUSICS SET RATING = RATING +"+dif+" WHERE ID = '"+musicId+"'";
                statement.executeUpdate(sql);
                sql = "INSERT INTO VOTES (MUSIC_ID , VOTE, IP) VALUES (?,?,?)";
                PreparedStatement pstm = connection.prepareStatement(sql);
                pstm.setString(1,musicId);
                pstm.setString(2,vote);
                pstm.setString(3,ip);
                pstm.executeUpdate();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public static void resetRating(String musicId){
        Connection connection =  getConnection();
        try {
            if (connection != null) {
                Statement statement = connection.createStatement();
                String sql = "UPDATE MUSICS SET RATING = 0  WHERE ID = '"+musicId+"'";
                statement.executeUpdate(sql);
                sql = "DELETE FROM VOTES WHERE MUSIC_ID = ?";
                PreparedStatement pstm = connection.prepareStatement(sql);
                pstm.setString(1,musicId);
                pstm.executeUpdate();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static List<Music> getMusics(){
        Connection connection = getConnection();
        try {
            if (connection != null) {

                Statement statement = connection.createStatement();
                String query = "SELECT ID, MUSIC_NAME, RATING, IMAGE_PATH, DURATION FROM MUSICS";
                ResultSet resultSet = statement.executeQuery(query);
                List<Music> ans = new ArrayList<>();
                while ( resultSet.next() ) {
                    String id = resultSet.getString("ID");
                    String  name = resultSet.getString("MUSIC_NAME");
                    int rating = resultSet.getInt("RATING");
                    int duration = resultSet.getInt("DURATION");
                    String imagePath = resultSet.getString("IMAGE_PATH");
                    Music music = new Music(duration,id,name,rating,imagePath);
                    ans.add(music);
                }
                connection.close();
                return ans;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getVote(String ip, String musicId) {
        Connection connection =  getConnection();
        try {
            if (connection != null) {
                String sql = "SELECT VOTE , IP FROM VOTES WHERE MUSIC_ID = ? AND IP = ? ";
                PreparedStatement pstm = connection.prepareStatement(sql);
                pstm.setString(1,musicId);
                pstm.setString(2,ip);
                ResultSet resultSet = pstm.executeQuery();
                if(resultSet.next()){
                    connection.close();
                    return resultSet.getString("VOTE");
                }
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "null";
    }
}
