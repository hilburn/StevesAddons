package stevesaddons.components;

import stevesaddons.helpers.StevesEnum;
import vswe.stevesfactory.components.ComponentMenuContainer;
import vswe.stevesfactory.components.FlowComponent;

import java.util.List;

public class ComponentMenuRF extends ComponentMenuContainer
{
    public ComponentMenuRF(FlowComponent parent)
    {
        super(parent, StevesEnum.RF_HANDLER);
    }

    public String getName() {
        return StevesEnum.TYPE_RF.toString();
    }

    @Override
    public void addErrors(List<String> errors) {
        if(this.selectedInventories.isEmpty() && this.isVisible()) {
            errors.add(StevesEnum.NO_RF_ERROR.toString());
        }
    }

    protected void initRadioButtons() {
    }
}
