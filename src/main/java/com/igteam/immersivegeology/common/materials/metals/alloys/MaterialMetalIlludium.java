package com.igteam.immersivegeology.common.materials.metals.alloys;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalBase;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalIlludium extends MaterialMetalBase
{

	@Override
	public String getName()
	{
		return "illudium";
	}

	@Nonnull
	@Override
	public String getModID()
	{
		return ImmersiveGeology.MODID;
	}

	@Override
	public EnumMetalType getMetalType()
	{
		return EnumMetalType.ALLOY;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(Arrays.asList(
			new ElementProportion(PeriodicTableElement.UNOBTANIUM),
			new ElementProportion(PeriodicTableElement.TITANIUM, 2),
			new ElementProportion(PeriodicTableElement.LUXUM)
		)
		);
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.UNCOMMON;
	}

	@Override
	public int getBoilingPoint()
	{
		return 3200;
	}

	@Override
	public int getMeltingPoint()
	{
		return 1698;
	}

	@Override
	public int getColor(int temperature)
	{
		return 0x717377;
	}

	@Override
	public float getHardness()
	{
		return 3;
	}

	@Override
	public float getMiningResistance()
	{
		return 8;
	}

	@Override
	public float getBlastResistance()
	{
		return 12;
	}

	@Override
	public float getDensity()
	{
		return 7.800f; // gm/cm^3
	}

	@Override
	public int getHeadDurability() {
		return 900;
	}

	@Override
	public int getHeadMiningLevel() {
		return 6;
	}

	@Override
	public int getHeadEnchantability() {
		return 25;
	}

	@Override
	public int getHeadMiningSpeed() {
		return 5;
	}

	@Override
	public int getHeadAttackSpeed() {
		return 4;
	}

	@Override
	public int getHeadAttackDamage() {
		return 4;
	}
}