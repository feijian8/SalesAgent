package ua.krem.agent.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Code implements Serializable{
	
	private String code;
	private String Name;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
