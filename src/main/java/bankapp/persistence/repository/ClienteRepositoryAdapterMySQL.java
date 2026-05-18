package bankapp.persistence.repository;

import bankapp.domain.Cliente;
import bankapp.services.outputport.ClientePersistencePort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

public class ClienteRepositoryAdapterMySQL implements ClientePersistencePort {

    private Connection connection;

    public ClienteRepositoryAdapterMySQL(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Cliente saveCliente(Cliente cliente) {

        String sql = "INSERT INTO cliente (identificacion, nombre, celular, usuario, contrasena, intentosFallidos, bloqueado) VALUES (?,?,?,?,?,?,?) ";

        try(PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setString(1, cliente.getIdentificacion());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getCelular());
            ps.setString(4, cliente.getUsuario());
            ps.setString(5, cliente.getContrasena());
            ps.setInt(6, cliente.getIntentosFallidos());
            ps.setBoolean(7, cliente.isBloqueado());

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()){
            cliente.setId(keys.getInt(1));
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Error al sincronizar información en la Base De Datos");
        }

        return cliente;
    }

    @Override
    public List<Cliente> findAllClientes() {
        return List.of();
    }

    @Override
    public Optional<Cliente> findById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Cliente> findByUsuario(String usuario) {

        String sql = "SELECT * FROM cliente WHERE usuario = ?";

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, usuario);

        ResultSet rs = ps.executeQuery();
        if (rs.next()){
            Cliente c = new Cliente(
                    rs.getInt("id"),
                    rs.getString("identificacion"),
                    rs.getString("nombre"),
                    rs.getString("celular"),
                    rs.getString("usuario"),
                    rs.getString("contrasena")
            );
            c.setBloqueado(rs.getBoolean("bloqueado"));
            c.setIntentosFallidos(rs.getInt("intentosFallidos"));


            return Optional.of(c);

        }
        }catch (Exception e){
            System.out.println("Error");
        } return Optional.empty();

    }

    @Override
    public boolean existeUsuario(String usuario) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }

    @Override
    public Optional<Cliente> updateCliente(Cliente clienteActualizado) {
        return Optional.empty();
    }
}
