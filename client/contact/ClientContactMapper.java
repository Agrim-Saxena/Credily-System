package com.credv3.client.contact;

import com.credv3.common.entities.ClientContact;

public class ClientContactMapper {

	public ClientContact mapContact(ClientContactRequest request) {
		
		ClientContact clientContact = new ClientContact();
		
		clientContact.setFirstName(request.getFirstName());
		clientContact.setLastName(request.getLastName());
		clientContact.setPhone(request.getPhone());
		clientContact.setAlternatePhone(request.getAlternatePhone());
		clientContact.setEmail(request.getEmail());
		clientContact.setIsPrimary(request.getIsPrimary());
		
		return clientContact;
	}
}
