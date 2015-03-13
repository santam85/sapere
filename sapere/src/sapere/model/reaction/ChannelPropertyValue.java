package sapere.model.reaction;

import java.nio.channels.*;


public class ChannelPropertyValue extends PropertyValue {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6075143379480349106L;

	public enum ChannelType{
		in,out;
	}
	
	private Channel channel;
	private String channelId;
	private ChannelType direction;
	
	public ChannelPropertyValue(String channelId, Channel channel, ChannelType direction){
		this.channel = channel;
		this.channelId = channelId;
		this.direction = direction;
	}
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public String toString(){
		return "#chan("+channelId+")."+direction;
	}

	@Override
	public PropertyValue getCopy() {
		return new ChannelPropertyValue(channelId,channel,direction);
	}

	@Override
	public boolean equals(Object p) {
		// TODO Auto-generated method stub
		return false;
	}
}
