package net.blaklizt.streets.persistence;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 9/8/13
 * Time: 3:45 PM
 */
public class ModuleTimePK implements Serializable {
	private Long moduleId;
	private String runtime;

	@Id@Column(name = "ModuleID")
	public Long getModuleID() {
		return moduleId;
	}

	public void setModuleID(Long moduleId) {
		this.moduleId = moduleId;
	}

	@Id@Column(name = "Runtime")
	public String getRuntime() {
		return runtime;
	}

	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ModuleTimePK that = (ModuleTimePK) o;

		if (runtime != null ? !runtime.equals(that.runtime) : that.runtime != null) return false;
		if (moduleId != null ? !moduleId.equals(that.moduleId) : that.moduleId != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = moduleId != null ? moduleId.hashCode() : 0;
		result = 31 * result + (runtime != null ? runtime.hashCode() : 0);
		return result;
}}
