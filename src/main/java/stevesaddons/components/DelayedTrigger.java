package stevesaddons.components;

import stevesaddons.helpers.StevesEnum;
import vswe.stevesfactory.components.ComponentMenuInterval;
import vswe.stevesfactory.components.FlowComponent;


public class DelayedTrigger extends ComponentMenuInterval
{
    public DelayedTrigger(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public String getName()
    {
        return StevesEnum.DELAY.toString();
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() == StevesEnum.DELAYED;
    }

    public void setTimer()
    {
        
    }
}
