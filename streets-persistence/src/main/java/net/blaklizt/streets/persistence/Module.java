package net.blaklizt.streets.persistence;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 11/10/13
 * Time: 6:29 PM
 */
@Entity
@Table(name = "Module")
public class Module {
	private Long moduleId;

	@Column(name = "ModuleID")
	@Id
	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	private String moduleName;

	@Column(name = "ModuleName")
	@Basic
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private Boolean enabled;

	@Column(name = "Enabled")
	@Basic
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

//	@OneToMany
//	@JoinTable(name="Module")
//	@JoinColumn(name="ModuleID", referencedColumnName="ModuleID")
//	private List<ModuleTime> moduleTime;
//
//	public List<ModuleTime> getModuleTime() {
//		return moduleTime;
//	}
//
//	public void setModule(List<ModuleTime> moduleTime) {
//		this.moduleTime = moduleTime;
//	}
}
