package se.l4.commons.id;

import java.time.Clock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link LongIdGenerator} that uses the current time and a local sequence
 * to create identifiers. This generator guarantees that identifiers are
 * always increasing, but is not suitable for distributed usage.
 *
 * @author Andreas Holstenson
 *
 */
public class SequenceLongIdGenerator
	implements LongIdGenerator
{
	public static final int MAX_SEQUENCE = (int) Math.pow(2, 21);

	private int sequence;
	private long lastTime;

	private final Clock clock;
	private final Lock lock;

	public SequenceLongIdGenerator()
	{
		this(Clock.systemUTC());
	}

	public SequenceLongIdGenerator(Clock clock)
	{
		this.clock = clock;
		lock = new ReentrantLock();
	}

	@Override
	public long next()
	{
		lock.lock();
		try
		{
			long time = clock.millis() - SimpleLongIdGenerator.EPOCH_START;
			if(lastTime > time)
			{
				// Time is moving backwards, refuse to generate anything
				throw new Error("Time on system is moving backwards, please update system configuration");
			}

			if(sequence >= MAX_SEQUENCE)
			{
				// We have reached our maximum number of ids for this ms
				// This is very unlikely so this is pretty untested
				long timeRightNow;
				do
				{
					try
					{
						Thread.sleep(1);
					}
					catch(InterruptedException e)
					{
						Thread.currentThread().interrupt();
						throw new RuntimeException("Interrupted id generation; " + e.getMessage(), e);
					}

					timeRightNow = clock.millis() - SimpleLongIdGenerator.EPOCH_START;
				}
				while(timeRightNow == time);

				time = timeRightNow;
			}

			if(lastTime != time)
			{
				lastTime = time;
				sequence = 0;
			}

			sequence++;

			return (time << 22) | sequence;
		}
		finally
		{
			lock.unlock();
		}
	}
}
