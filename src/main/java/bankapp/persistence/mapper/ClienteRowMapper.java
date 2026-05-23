package bankapp.persistence.mapper;

import bankapp.domain.Cliente;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteRowMapper implements RowMapper<Cliente> {

    @Override
    public Cliente mapRow(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getInt("id"),
                rs.getString("identificacion"),
                rs.getString("nombre"),
                rs.getString("celular"),
                rs.getString("usuario"),
                rs.getString("contrasena")
        );
        cliente.setBloqueado(rs.getBoolean("bloqueado"));
        cliente.setIntentosFallidos(rs.getInt("intentosFallidos"));
        return cliente;
    }
}
