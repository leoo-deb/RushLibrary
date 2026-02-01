package model;

public class Auditoria {
    private Integer id;
    private String tipo;
    private Integer ID_FUNCIONARIO;
    private Integer ID_TIPO;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getID_FUNCIONARIO() {
        return ID_FUNCIONARIO;
    }

    public void setID_FUNCIONARIO(Integer ID_FUNCIONARIO) {
        this.ID_FUNCIONARIO = ID_FUNCIONARIO;
    }

    public Integer getID_TIPO() {
        return ID_TIPO;
    }

    public void setID_TIPO(Integer ID_TIPO) {
        this.ID_TIPO = ID_TIPO;
    }
}
