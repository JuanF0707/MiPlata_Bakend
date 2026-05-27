package bankapp.persistence.mapper;

import bankapp.domain.Cuenta;
import bankapp.domain.CuentaAhorros;
import bankapp.domain.CuentaCorriente;
import bankapp.domain.TarjetaCredito;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CuentaRowMapper implements RowMapper<Cuenta>{
    @Override
    public Cuenta mapRow(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        String numeroCuenta = rs.getString("numero_cuenta");
        double saldo = rs.getDouble("saldo");

        Cuenta cuenta;
        if (tipo.equals("AHORROS")) {
            cuenta = new CuentaAhorros(numeroCuenta, saldo, rs.getDouble("tasa_interes"));
        } else if (tipo.equals("TARJETA")) {
            TarjetaCredito tc = new TarjetaCredito(numeroCuenta, rs.getDouble("cupo"));
            cuenta = tc;
        } else {
            cuenta = new CuentaCorriente(numeroCuenta, saldo);
        }
        cuenta.setId(rs.getInt("id"));
        return cuenta;
    }
}
