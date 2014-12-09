package stevesaddons.components;

import cofh.api.energy.IEnergyHandler;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyFacingHolder
{
    private IEnergyHandler energyHandler;
    private ForgeDirection accessibleFace;

    public EnergyFacingHolder(IEnergyHandler energyHandler, ForgeDirection direction)
    {
        this.energyHandler = energyHandler;
        this.accessibleFace = direction;
    }

    public IEnergyHandler getEnergyHandler()
    {
        return energyHandler;
    }

    public ForgeDirection getAccessibleFace()
    {
        return accessibleFace;
    }

    public int extract(int amount, boolean simulate)
    {
        return energyHandler.extractEnergy(accessibleFace, amount, simulate);
    }
}
