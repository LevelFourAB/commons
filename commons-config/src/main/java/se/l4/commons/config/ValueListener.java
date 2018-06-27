package se.l4.commons.config;

public interface ValueListener<T>
{
	void valueChanged(String key, T oldValue, T newValue);
}
