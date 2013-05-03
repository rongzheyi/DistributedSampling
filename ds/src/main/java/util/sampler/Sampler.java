package util.sampler;

/**
 * weighted random sampling
 * @author zheyi
 *
 */
public interface Sampler <T>
{
	public boolean sample(T item, double weight);
	
	public T getItem();
	public double getKey();

}
