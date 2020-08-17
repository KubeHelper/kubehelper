package com.kubehelper.viewmodel;

public class PageModel<T> {
	private String uri;
	private T data;

	public PageModel(String uri, T data) {
		this.uri = uri;
		this.data = data;
	}

	public String getUri() {
		return uri;
	}

	public T getData() {
		return data;
	}
}
