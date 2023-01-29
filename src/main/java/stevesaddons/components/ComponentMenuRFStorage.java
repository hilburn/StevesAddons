package stevesaddons.components;

import java.util.EnumSet;
import java.util.List;

import stevesaddons.helpers.StevesEnum;
import vswe.stevesfactory.blocks.ConnectionBlockType;
import vswe.stevesfactory.components.ComponentMenuContainer;
import vswe.stevesfactory.components.FlowComponent;

public class ComponentMenuRFStorage extends ComponentMenuContainer {

    public ComponentMenuRFStorage(FlowComponent parent) {
        super(parent, StevesEnum.RF_CONNECTION);
    }

    public String getName() {
        return StevesEnum.TYPE_RF.toString();
    }

    @Override
    public void addErrors(List<String> errors) {
        if (this.selectedInventories.isEmpty() && this.isVisible()) {
            errors.add(StevesEnum.NO_RF_ERROR.toString());
        }
    }

    @Override
    protected EnumSet<ConnectionBlockType> getValidTypes() {
        return EnumSet.of(StevesEnum.RF_PROVIDER, StevesEnum.RF_RECEIVER);
    }

    protected void initRadioButtons() {}
}
