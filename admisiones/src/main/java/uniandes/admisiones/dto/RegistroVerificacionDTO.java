package uniandes.admisiones.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class RegistroVerificacionDTO {

	private Long pidm;
	private String term_code;
	private String appl_no;
	private String amr_code;
	private Date receive_date;
	private String comment;

}
