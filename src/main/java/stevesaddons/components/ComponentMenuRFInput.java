package stevesaddons.components;

import java.util.List;

import stevesaddons.helpers.StevesEnum;
import vswe.stevesfactory.components.FlowComponent;
import vswe.stevesfactory.network.DataReader;

public class ComponentMenuRFInput extends ComponentMenuRF {

    public ComponentMenuRFInput(FlowComponent parent) {
        super(parent, StevesEnum.RF_PROVIDER);
    }

    public String getName() {
        return StevesEnum.TYPE_RF_INPUT.toString();
    }

    @Override
    public void addErrors(List<String> errors) {
        if (this.selectedInventories.isEmpty() && this.isVisible()) {
            errors.add(StevesEnum.NO_RF_ERROR.toString());
        }
    }

    @Override
    public void readNetworkComponent(DataReader dr) {
        super.readNetworkComponent(dr);
        updateConnectedNodes();
    }

    protected void initRadioButtons() {}
}
