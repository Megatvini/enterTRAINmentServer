package ge.edu.freeuni.android.entertrainment.server.services.music.DO;


import ge.edu.freeuni.android.entertrainment.server.services.music.data.Music;

import java.sql.*;

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
                "MUSIC_DATA    BYTEA           NOT NULL )";

        String createVotesTable = "CREATE TABLE IF NOT EXISTS VOTES  " +
                "(ID INT     PRIMARY KEY     NOT NULL," +
                "MUSIC_ID       TEXT             NOT NULL REFERENCES MUSICS(ID), " +
                "VOTE           TEXT            NOT NULL," +
                "IP             TEXT            NOT NULL )";


        if (stmt != null) {
            stmt.executeUpdate(createMusicTable);
            stmt.executeUpdate(createVotesTable);
        }


    }

    public static Music getMusic(String musicId){
        Connection connection = getConnection();
        try {
            if (connection != null) {

                Statement statement = connection.createStatement();
                String query = "SELECT ID, MUSIC_NAME, RATING, IMAGE_PATH FROM MUSICS WHERE ID = '"+musicId+"'";
                ResultSet resultSet = statement.executeQuery(query);
                if ( resultSet.next() ) {
                    String id = resultSet.getString("ID");
                    String  name = resultSet.getString("MUSIC_NAME");
                    int rating = resultSet.getInt("RATING");
                    String imagePath = resultSet.getString("IMAGE_PATH");
                    return new Music(id,name,rating,imagePath);
                }
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
                String sql = "INSERT INTO MUSICS (MUSIC_NAME,IMAGE_PATH,RATING, MUSIC_DATA, ID) "
                        + "VALUES (?,?,?,?,?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1,music.getName());
                statement.setString(2,music.getImagePath());
                statement.setInt(3,music.getRating());
                statement.setBytes(4,bytes);
                statement.setString(5,music.getId());
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

}
