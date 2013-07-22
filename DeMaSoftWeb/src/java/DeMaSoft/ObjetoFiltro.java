/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DeMaSoft;

/**
 *
 * @author ladmin
 */
public class ObjetoFiltro {
    private String _numPlaca;
    private Boolean _autos;
    private Boolean _motos;
    private Boolean _ida;
    private Boolean _vuelta;
    private Boolean _estaAlmorzando;
    private Integer _idChofer;
    private Integer _idCliente;
    private Integer _idPedido;
    
    public String getNumPlaca() {
        return _numPlaca;
    }
    
    public ObjetoFiltro(){
        _numPlaca ="";
        _autos=true;
        _motos=true;
        _ida =true;
        _vuelta=true;
        _estaAlmorzando=false;
    }

    public void setNumPlaca(String numPlaca) {
        this._numPlaca = numPlaca;
    }

    public Boolean getAutos() {
        return _autos;
    }

    public void setAutos(Boolean autos) {
        this._autos = autos;
    }

    public Boolean getMotos() {
        return _motos;
    }

    public void setMotos(Boolean motos) {
        this._motos = motos;
    }

    public Boolean getIda() {
        return _ida;
    }

    public void setIda(Boolean ida) {
        this._ida = ida;
    }

    public Boolean getVuelta() {
        return _vuelta;
    }

    public void setVuelta(Boolean vuelta) {
        this._vuelta = vuelta;
    }

    public Boolean getEstaAlmorzando() {
        return _estaAlmorzando;
    }

    public void setEstaAlmorzando(Boolean estaAlmorzando) {
        this._estaAlmorzando = estaAlmorzando;
    }

    public Integer getIdChofer() {
        return _idChofer;
    }

    public void setIdChofer(Integer idChofer) {
        this._idChofer = idChofer;
    }

    public Integer getIdCliente() {
        return _idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this._idCliente = idCliente;
    }
}
