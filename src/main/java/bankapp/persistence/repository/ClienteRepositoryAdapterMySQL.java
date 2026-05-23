package bankapp.persistence.repository;

import bankapp.domain.*;
import bankapp.persistence.mapper.ClienteRowMapper;
import bankapp.persistence.mapper.RowMapper;
import bankapp.services.outputport.ClientePersistencePort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class ClienteRepositoryAdapterMySQL implements ClientePersistencePort {

    private Connection connection;
    private final ClienteRowMapper clienteRowMapper = new ClienteRowMapper();

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
            saveCuentas(cliente);    // Llamo al metodo de saveCuentas desde saveCliente

        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Error al sincronizar información en la Base De Datos");
        }

        return cliente;
    }

    // agrego metodo para guardar en el servidor las 3 cuentas que se crean de un usuario nuevo
    private void saveCuentas(Cliente cliente){
        for (Cuenta cuenta : cliente.getCuentas()){

            String tipo = "";
            double tasaInteres = 0;
            double cupo = 0;
            double deuda = 0;

            if (cuenta instanceof CuentaAhorros){
                // Ahorros
                CuentaAhorros ca = (CuentaAhorros) cuenta;
                tipo = "AHORROS";
                tasaInteres = ca.getTasaInteres();

            } else if (cuenta instanceof TarjetaCredito) {
                // Tarjeta Credito
                TarjetaCredito tc = (TarjetaCredito) cuenta;
                tipo = "TARJETA";
                cupo = tc.getCupo();
                deuda = tc.getDeuda();
            } else {
                // Corriente
                 tipo = "CORRIENTE";

            }
            String sql = "INSERT INTO cuentas (numero_cuenta, tipo, saldo, estado, tasa_interes, cupo, deuda, cliente_id) VALUES (?,?,?,?,?,?,?,?)";

            try(PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setString(1, cuenta.getNumeroCuenta());
                ps.setString(2, tipo);
                ps.setDouble(3, cuenta.getSaldo());
                ps.setString(4, cuenta.getEstado().name());
                ps.setDouble(5, tasaInteres);
                ps.setDouble(6, cupo);
                ps.setDouble(7, deuda);
                ps.setInt(8, cliente.getId());

                ps.executeUpdate();

            } catch (Exception e){
                System.out.println("Error al guardar las cuentas del usuario");
            }
        }
    }


    @Override
    public List<Cliente> findAllClientes() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT c.*, cu.numero_cuenta, cu.tipo, cu.saldo, cu.tasa_interes, cu.cupo, cu.deuda " +
                "FROM cliente c LEFT JOIN cuentas cu ON c.id = cu.cliente_id";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            Map<Integer, Cliente> mapa = new LinkedHashMap<>();
            while(rs.next()){
                int id = rs.getInt("id");
                if(!mapa.containsKey(id)){
                    Cliente c = clienteRowMapper.mapRow(rs);
                    mapa.put(id, c);
                }
                String tipo = rs.getString("tipo");
                if(tipo != null){
                    Cliente c = mapa.get(id);
                    if(tipo.equals("AHORROS")){
                        c.agregarCuenta(new CuentaAhorros(rs.getString("numero_cuenta"), rs.getDouble("saldo"), rs.getDouble("tasa_interes")));
                    } else if(tipo.equals("TARJETA")){
                        c.agregarCuenta(new TarjetaCredito(rs.getString("numero_cuenta"), rs.getDouble("cupo")));
                    } else {
                        c.agregarCuenta(new CuentaCorriente(rs.getString("numero_cuenta"), rs.getDouble("saldo")));
                    }
                }
            }
            lista.addAll(mapa.values());
        } catch(Exception e){
            System.out.println("Error al listar clientes");
        }
        return lista;
    }

    @Override
    public Optional<Cliente> findById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Cliente> findByUsuario(String usuario) {
        String sql = "SELECT c.*, cu.id as cuenta_id, cu.numero_cuenta, cu.tipo, cu.saldo, cu.estado, cu.tasa_interes, cu.cupo, cu.deuda " +
                "FROM cliente c LEFT JOIN cuentas cu ON c.id = cu.cliente_id " +
                "WHERE c.usuario = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            Cliente cliente = null;
            while(rs.next()){
                if(cliente == null){
                    cliente = clienteRowMapper.mapRow(rs);
                }
                String tipo = rs.getString("tipo");
                if(tipo != null){
                    Cuenta cuenta = null;
                    if(tipo.equals("AHORROS")){
                        cuenta = new CuentaAhorros(rs.getString("numero_cuenta"), rs.getDouble("saldo"), rs.getDouble("tasa_interes"));
                    } else if(tipo.equals("TARJETA")){
                        cuenta = new TarjetaCredito(rs.getString("numero_cuenta"), rs.getDouble("cupo"));
                    } else {
                        cuenta = new CuentaCorriente(rs.getString("numero_cuenta"), rs.getDouble("saldo"));
                    }
                    cuenta.setId(rs.getInt("cuenta_id"));
                    cliente.agregarCuenta(cuenta);
                }
            }
            if(cliente != null) return Optional.of(cliente);
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Error al buscar cliente");
        }
        return Optional.empty();
    }

    @Override
    public boolean existeUsuario(String usuario) {

        String sql = "SELECT COUNT(*) FROM cliente WHERE usuario = ?";

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, usuario);

            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return rs.getInt(1) >0;
            }
        } catch (Exception e){
            System.out.println("Error al verificar usuario");
        }
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM cliente WHERE id = ? ";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (Exception e){
            System.out.println("Error al eliminar cliente");
        }
        return false;
    }

    @Override
    public Optional<Cliente> updateCliente(Cliente clienteActualizado) {

        String sql = "UPDATE cliente SET nombre = ?, celular = ? WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, clienteActualizado.getNombre());
            ps.setString(2, clienteActualizado.getCelular());
            ps.setInt(3, clienteActualizado.getId());
            int filas = ps.executeUpdate();
            if (filas > 0) return Optional.of(clienteActualizado);
        } catch (Exception e){
            System.out.println("Error al actualizar cliente");
        }
        return Optional.empty();
    }
}
