package br.com.actionlabs.carboncalc.util;

import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseStandartDTO {
    private String title;
    private String message;

    public ResponseStandartDTO(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String toJson(String callback) {
        JSONPObject jsonpObject = new JSONPObject(callback, this);
        return jsonpObject.toString();
    }

    public static ResponseStandartDTO failed(String mensagem) {
        return new ResponseStandartDTO("Failed.", mensagem);
    }

    public static ResponseStandartDTO success(String mensagem) {
        return new ResponseStandartDTO("Success", mensagem);
    }
}
