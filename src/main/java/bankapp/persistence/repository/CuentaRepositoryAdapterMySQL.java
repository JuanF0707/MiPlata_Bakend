package bankapp.persistence.repository;

import bankapp.domain.Cuenta;
import bankapp.persistence.mapper.CuentaRowMapper;
import bankapp.services.outputport.CuentaPersistencePort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CuentaRepositoryAdapterMySQL implements CuentaPersistencePort {


    private final Connection connection;
    private final CuentaRowMapper cuentaRowMapper = new CuentaRowMapper();

    public CuentaRepositoryAdapterMySQL(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Cuenta saveCuenta(Cuenta cuenta, int clienteId) {
        String sql = "INSERT INTO cuentas (numero_cuenta, tipo, saldo, estado, tasa_interes, cupo, deuda, cliente_id) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cuenta.getNumeroCuenta());
            ps.setString(2, cuenta.getClass().getSimpleName()
                    .replace("CuentaAhorros", "AHORROS")
                    .replace("CuentaCorriente", "CORRIENTE")
                    .replace("TarjetaCredito", "TARJETA"));
            ps.setDouble(3, cuenta.getSaldo());
            ps.setString(4, cuenta.getEstado().name());
            ps.setDouble(5, cuenta instanceof bankapp.domain.CuentaAhorros
                    ? ((bankapp.domain.CuentaAhorros) cuenta).getTasaInteres() : 0);
            ps.setDouble(6, cuenta instanceof bankapp.domain.TarjetaCredito
                    ? ((bankapp.domain.TarjetaCredito) cuenta).getCupo() : 0);
            ps.setDouble(7, cuenta instanceof bankapp.domain.TarjetaCredito
                    ? ((bankapp.domain.TarjetaCredito) cuenta).getDeuda() : 0);
            ps.setInt(8, clienteId);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) cuenta.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar cuenta", e);
        }
        return cuenta;
    }

    @Override
    public List<Cuenta> findByClienteId(int clienteId) {
        List<Cuenta> lista = new ArrayList<>();
        String sql = "SELECT * FROM cuentas WHERE cliente_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(cuentaRowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar cuentas");
        }
        return lista;
    }

    @Override
    public boolean updateSaldo(int cuentaId, double nuevoSaldo) {
        String sql = "UPDATE cuentas SET saldo = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, nuevoSaldo);
            ps.setInt(2, cuentaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar saldo");
        }
        return false;
    }

    @Override
    public boolean deleteByClienteId(int clienteId) {
        String sql = "DELETE FROM cuentas WHERE cliente_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar cuentas");
        }
        return false;
    }
}
