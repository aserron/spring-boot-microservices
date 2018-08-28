/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.merchant.exc.dto;
import lombok.Data;

@Data
class ApiErrorStatus {

    private int code;
    private String name;
    private String reason;
}
