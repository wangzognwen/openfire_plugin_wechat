package com.wangzhe.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class GroupEntities.
 */
@XmlRootElement(name = "groups")
public class GroupEntities {
	
	/** The groups. */
	List<GroupEntity> groups;

	/**
	 * Instantiates a new group entities.
	 */
	public GroupEntities() {
	}

	/**
	 * Instantiates a new group entities.
	 *
	 * @param groups the groups
	 */
	public GroupEntities(List<GroupEntity> groups) {
		this.groups = groups;
	}

	/**
	 * Gets the groups.
	 *
	 * @return the groups
	 */
	@XmlElement(name = "group")
	public List<GroupEntity> getGroups() {
		return groups;
	}

	/**
	 * Sets the groups.
	 *
	 * @param groups the new groups
	 */
	public void setGroups(List<GroupEntity> groups) {
		this.groups = groups;
	}

}
