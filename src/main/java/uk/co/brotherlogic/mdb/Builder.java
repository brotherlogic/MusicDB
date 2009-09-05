package uk.co.brotherlogic.mdb;

/**
 * An interface for creating an object
 * 
 * @author sat
 * 
 * @param <X>
 */
public interface Builder<X>
{
	X build(String name);
}
