package login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class User {

    // Centralizando a URL do banco para organizar melhor o código
    private final String DB_URL = "jdbc:mysql://127.0.0.1/test?user=lopes&password=123";

    public Connection conectarBD() {
        try {
            // Driver atualizado conforme as novas versões do MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            System.err.println("Falha na conexão com o banco de dados: " + e.getMessage());
            return null;
        }
    }

    public boolean verificarUsuario(String login, String senha) {
        
        // Bloqueio inicial para evitar processamento se vier algo vazio
        if (login == null || senha == null) {
            return false;
        }

        String query = "SELECT nome FROM usuarios WHERE login = ? AND senha = ?";
        boolean autenticado = false;

        // Uso do try-with-resources: fecha automaticamente o banco, o Statement e o ResultSet!
        try (Connection conn = conectarBD()) {
            
            // Tratando o NullPointer se o banco cair
            if (conn == null) {
                System.err.println("O serviço de banco de dados está fora do ar.");
                return false;
            }

            // Usando PreparedStatement contra injeção SQL
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, login);
                stmt.setString(2, senha);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        autenticado = true;
                        System.out.println("Login efetuado. Bem-vindo(a), " + rs.getString("nome"));
                    }
                }
            }
        } catch (Exception e) {
            // Logando qualquer erro no terminal
            System.err.println("Erro ao validar credenciais do usuário: " + e.getMessage());
        }

        return autenticado;
    }
}