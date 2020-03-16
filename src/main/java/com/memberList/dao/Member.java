package com.memberList.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "T_Member_Basis")
public class Member {
	@Id
	@GeneratedValue (strategy= GenerationType.IDENTITY)
	private int memberId;
	private String companyName;//
	private String basisMembershipNo;//
	private String YearOfEstablishment;
	private String address;//
	private String city;
	private String postcode;
	private String contactNo;//
	private String emai;//
	private String companyWebsite;//
	private String otherWebsitesThatBelongsToTheCompany;
	private String organizationsHeadInBangladesh;//
	private String designation;//
	private String mobile;//
	private String legalStructureOfTheCompany;

	
}
