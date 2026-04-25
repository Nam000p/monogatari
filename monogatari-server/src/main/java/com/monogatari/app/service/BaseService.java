package com.monogatari.app.service;

import com.monogatari.app.entity.User;

public abstract class BaseService {
	protected abstract UserService getUserService();
	
	protected User getCurrentUser() {
		return getUserService().getCurrentAuthenticateUser();
	}
}