package bankapp.persistence.repository;

import bankapp.domain.Movimiento;
import bankapp.domain.enums.TipoMovimiento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MovimientoRepositoryAdapterMySQL {

    private Connection connection;

    public MovimientoRepositoryAdapterMySQL(Connection connection) {
        this.connection = connection;
    }

    // Agrego metodo para guardar movimientos en mySQL
    public void save(Movimiento movimiento, int cuentaId){
        String sql = "INSERT INTO movimientos (tipo, valor, saldo_posterior, descripcion, cuenta_id) VALUES (?,?,?,?,?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, movimiento.getTipo().name());
            ps.setDouble(2, movimiento.getValor());
            ps.setDouble(3, movimiento.getSaldoPosterior());
            ps.setString(4, movimiento.getDescripcion());
            ps.setInt(5, cuentaId);
            ps.executeUpdate();
        } catch (Exception e){
            System.out.println("Error al guardar movimiento");
        }
    }

    public List<Movimiento> findByCuentaId(int cuentaId){
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimientos WHERE cuenta_id = ? ORDER BY fecha DESC";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, cuentaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Movimiento m = new Movimiento(
                        TipoMovimiento.valueOf(rs.getString("tipo")),
                        rs.getDouble("valor"),
                        rs.getDouble("saldo_posterior"),
                        rs.getString("descripcion")
                );
                lista.add(m);
            }
        }catch (Exception e){
            System.out.println("Error al obtener movimientos");
        }
        return lista;
    }
}
