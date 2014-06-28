package net.blaklizt.streets.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 11/10/13
 * Time: 6:29 PM
 */
@javax.persistence.IdClass(net.blaklizt.streets.persistence.ModuleTimePK.class)
@Entity
@Table(name = "ModuleTime")
public class ModuleTime implements Serializable {
	private Long moduleId;
	private String runtime;

	@Column(name = "ModuleID")
	@Id
	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	@Column(name="Runtime")
	@Id
	public String getRuntime() {
		return runtime;
	}

	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

//	@ManyToOne(optional = false)
//	@JoinTable(name="Module")
//	@JoinColumn(name="ModuleID", referencedColumnName="ModuleID")
//	private Module module;
//
//	public Module getModule() {
//		return module;
//	}
//
//	public void setModule(Module module) {
//		this.module = module;
//	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ModuleTime moduleTime = (ModuleTime) o;

		if (moduleId != null ? !moduleId.equals(moduleTime.moduleId) : moduleTime.moduleId != null) return false;
		if (runtime != null ? !runtime.equals(moduleTime.runtime) : moduleTime.runtime != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = moduleId != null ? moduleId.hashCode() : 0;
		result = 31 * result + (runtime != null ? runtime.hashCode() : 0);
		return result;
	}
}
