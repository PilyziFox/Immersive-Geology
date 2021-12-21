package generators.recipe;


import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.BlastFurnaceRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.CrusherRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.MetalPressRecipeBuilder;
import blusunrize.immersiveengineering.common.items.IEItems;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.crafting.recipes.builders.BloomeryRecipeBuilder;
import com.igteam.immersive_geology.api.crafting.recipes.builders.CrystalizerRecipeBuilder;
import com.igteam.immersive_geology.api.crafting.recipes.builders.SeparatorRecipeBuilder;
import com.igteam.immersive_geology.api.crafting.recipes.builders.VatRecipeBuilder;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.IGProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGBloomeryProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGCraftingProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.ProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGCrystalizerProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGReductionProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGVatProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.api.materials.material_data.fluids.slurry.MaterialSlurryWrapper;
import com.igteam.immersive_geology.api.tags.IGTags;
import com.igteam.immersive_geology.common.fluid.IGFluid;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.igteam.immersive_geology.api.tags.IGTags.createItemWrapper;

public class IGRecipeProvider extends RecipeProvider {
    private final HashMap<String, Integer> PATH_COUNT = new HashMap<>();
    private final Path ADV_ROOT;

    private final Logger log = ImmersiveGeology.getNewLogger();

    public IGRecipeProvider(DataGenerator gen) {
        super(gen);
        ADV_ROOT = gen.getOutputFolder().resolve("data/minecraft/advancements/recipes/root.json");
    }

    @Nonnull
    @Override
    public String getName() {
        return super.getName() + ": " + IGLib.MODID;
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        CrusherRecipeBuilder crusherBuilder;

        for (MaterialEnum wrapper : MaterialEnum.values()) {
            Material material = wrapper.getMaterial();

            IGTags.MaterialTags tags = IGTags.getTagsFor(material);

            Item nugget = MaterialUseType.NUGGET.getItem(material);
            Item ingot = MaterialUseType.INGOT.getItem(material);
            Item dust = MaterialUseType.DUST.getItem(material);

            if (material.hasSubtype(MaterialUseType.NUGGET) && material.hasSubtype(MaterialUseType.INGOT)) {
                add3x3Conversion(ingot, nugget, tags.nugget, consumer);
                log.debug("Generated Ingot/Nugget Recipe for: " + material.getName());

                Block metal_block = MaterialUseType.STORAGE_BLOCK.getBlock(material);
                if (metal_block != null) {
                    add3x3Conversion(metal_block, ingot, tags.ingot, consumer);
                    log.debug("Generate Storage Block recipe for: " + material.getName());
                }

                if (!material.preExists()) {
                    crusherBuilder = CrusherRecipeBuilder.builder(tags.dust, 1);
                    crusherBuilder.addCondition(getTagCondition(tags.dust)).addCondition(getTagCondition(tags.ingot));
                    crusherBuilder.addInput(tags.ingot)
                            .setEnergy(3000)
                            .build(consumer, toRL("crusher/ingot_" + material.getName()));
                }
            }

            if (material.hasSubtype(MaterialUseType.PLATE)) {
                MetalPressRecipeBuilder.builder(IEItems.Molds.moldPlate, tags.plate, 1).addInput(tags.ingot)
                        .setEnergy(2400).build(consumer, toRL("metalpress/plate_" + material.getName()));
            }

            if (material.hasSubtype(MaterialUseType.ROD)) {
                MetalPressRecipeBuilder.builder(IEItems.Molds.moldRod, tags.rod, 1).addInput(tags.ingot)
                        .setEnergy(2400).build(consumer, toRL("metalpress/rod_" + material.getName()));
            }

            if (material.hasSubtype(MaterialUseType.GEAR)) {
                MetalPressRecipeBuilder.builder(IEItems.Molds.moldGear, tags.gear, 1)
                        .addInput(new IngredientWithSize(tags.ingot, 4))
                        .setEnergy(2400).build(consumer, toRL("metalpress/gear_" + material.getName()));
            }

            if (material.hasSubtype(MaterialUseType.WIRE)) {
                MetalPressRecipeBuilder.builder(IEItems.Molds.moldWire, tags.wire, 2).addInput(tags.ingot)
                        .setEnergy(2400).build(consumer, toRL("metalpress/wire_" + material.getName()));

            }

            for (MaterialEnum stone_base : MaterialEnum.stoneValues()) {
                if (material.hasSubtype(MaterialUseType.ORE_CHUNK)) {
                    Item ore_chunk = MaterialUseType.ORE_CHUNK.getItem(stone_base.getMaterial(), material);
                    Material processed_material = material;

                    if (material.getProcessedType() != null && material instanceof MaterialMetalBase && !((MaterialMetalBase) material).isNativeMetal()) {
                        processed_material = material.getProcessedType().getMaterial();
                    } else if (material.getProcessedType() != null) {
                        log.info("Material Processed Type not Null put is Native? " + material.getName());
                    }

                    if (processed_material != null) {
                        Item ore_ingot = MaterialUseType.INGOT.getItem(processed_material);

                        if (ore_ingot != null && ore_chunk != null) {
                            addBlastingRecipe(ore_chunk, ore_ingot, 0, 150, consumer);
                            log.debug("Generated Blasting Recipe for: " + ore_chunk.getRegistryName());
                        }
                    }

                    crusherBuilder = CrusherRecipeBuilder.builder(tags.dirty_ore_crushed, 1);
                    crusherBuilder.addInput(ore_chunk).setEnergy(6000).build(consumer, toRL("crusher/dirty_crushed_ore_" + material.getName()));
                }
            }
//
//            if(material.getMaterial().hasSubtype(MaterialUseType.CRUSHED_ORE) && material.getMaterial().hasSubtype(MaterialUseType.DUST))
//            {
//                MaterialEnum secondary_material = material.getMaterial().getSecondaryType();
//                crusherBuilder = CrusherRecipeBuilder.builder(tags.dust, 1);
//                if(secondary_material != null){
//                    Item secondary_out = MaterialUseType.DUST.getItem(secondary_material);
//                    if(secondary_out != null) {
//                        crusherBuilder.addSecondary(secondary_out, 0.33f);
//                    }
//                }
//                crusherBuilder.addInput(tags.ore_crushed).setEnergy(3000).build(consumer, toRL("crusher/ore_crushed_"+material.getMaterial().getName()));
//            }
        }

        for (FluidEnum fluidwrap: FluidEnum.values())
        {

            if (fluidwrap.getMaterial() instanceof MaterialSlurryWrapper) {continue;} //slurries registered not here

            IGMaterialProcess processess = fluidwrap.getMaterial().getProcessingMethod();
            if (processess != null) {
                Set<IGProcessingMethod> data = processess.getData();
                for (IGProcessingMethod method : data) {
                    if (method instanceof IGVatProcessingMethod){
                    addVatMethod(method, consumer);
                    }
                }
            }
        }

        for(MaterialEnum wrap : MaterialEnum.values()) {
            if(wrap.getMaterial() instanceof MaterialMineralBase) {
                MaterialMineralBase orebase = (MaterialMineralBase) wrap.getMaterial();
                //Gravity Separator
                ItemStack output = new ItemStack(IGRegistrationHolder.getItemByMaterial(orebase, MaterialUseType.CRUSHED_ORE));
                for (MaterialEnum stoneWrap : MaterialEnum.stoneValues()) {
                    Material stonebase = stoneWrap.getMaterial();
                    ItemStack input = new ItemStack(IGRegistrationHolder.getItemByMaterial(stonebase, orebase, MaterialUseType.DIRTY_CRUSHED_ORE));
                    ItemStack waste = new ItemStack(IGRegistrationHolder.getItemByMaterial(stonebase, MaterialUseType.ROCK_BIT));
                    SeparatorRecipeBuilder sepBuilder = new SeparatorRecipeBuilder();
                    sepBuilder.addResult(output).addInput(input).addWaste(waste).build(consumer, toRL("gravityseparator/wash_dirty_crushed_" + orebase.getName()));
                }
            }

            //Setup Recipe Generation for Mineral Processing
            Material base = wrap.getMaterial();

            //Create Recipe Builders
            IGMaterialProcess processess = base.getProcessingMethod();
            if(processess != null){
                Set<IGProcessingMethod> data = processess.getData();

                for (IGProcessingMethod method : data) {
                    switch (method.getKey()) {
                        case BLASTING:
                            addRedoxMethod(method,consumer);
                            break;
                        case CALCINATION:
                            break;
                        case ACID:
                            addVatMethod(method, consumer);
                            break;
                        case ROASTER:
                            addBloomeryMethod(method, consumer);
                            break;
                        case SEDIMENT:
                            break;
                        case ELECTROLYSIS:
                            addCrystalizerMethod(method,consumer);
                            break;
                        case CRAFTING:
                            addCraftingMethod(method, consumer);
                            break;
                    }
                }
            }
        }
        getSubRecipeProviders().forEach(subRecipeProvider -> subRecipeProvider.addRecipes(consumer));
    }

    private void addRedoxMethod(IGProcessingMethod method,  Consumer<IFinishedRecipe> consumer)
    {
        IGReductionProcessingMethod redoxMethod = (IGReductionProcessingMethod) method;
        int energyCost = redoxMethod.getEnergyCost();
        int processingTime = redoxMethod.getProcessingTime();
        ItemStack outputItem = redoxMethod.getOutputItem();
        ItemStack inputItem = redoxMethod.getInputItem();
        ItemStack slagItem = redoxMethod.getSlagItem();
        BlastFurnaceRecipeBuilder builder = BlastFurnaceRecipeBuilder.builder(outputItem);
        if (slagItem.isEmpty()) {
            builder.addSlag(IETags.slag, 1);
        }
        else{
            builder.addSlag(slagItem);
        }

        builder.addInput(inputItem).setEnergy(energyCost).setTime(processingTime)
                .build(consumer,toRL("ieblastfurnace/redox_recipe_" +
                        inputItem.getItem().getRegistryName().getPath().toLowerCase() + "_to_" +
                        outputItem.getItem().getRegistryName().getPath().toLowerCase() + "_and_"
                        + slagItem.getItem().getRegistryName().getPath().toLowerCase()));
    }

private void addCraftingMethod(IGProcessingMethod method, Consumer<IFinishedRecipe> consumer){
        assert method instanceof IGCraftingProcessingMethod;
        IGCraftingProcessingMethod m = (IGCraftingProcessingMethod) method;

        if(m.isShapeless()){
            ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapelessRecipe(m.getOutput().getItem(), m.getOutput().getCount());
            builder.addCriterion(m.getCriterionName(), hasItem(m.getCriterion()));

            for (Item item : m.getShapelessInputs()) {
                builder.addIngredient(item);
            }

            StringBuilder inputStrings = new StringBuilder();
            m.getShapelessInputs().stream().distinct().forEach((i) -> {
                inputStrings.append(i.getRegistryName().getPath().toLowerCase() + "_and_");
            });

            builder.build(consumer, toRL("crafting/" + inputStrings.toString() + "to_" + m.getOutput().getItem().getRegistryName().getPath().toLowerCase()));
            return;
        }

        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shapedRecipe(m.getOutput().getItem(), m.getOutput().getCount());
        builder.addCriterion(m.getCriterionName(), hasItem(m.getCriterion()));

        for (Character k : m.getPatternKeys()) {
            builder.key(k, m.getItemByKey(k));
        }
        builder.patternLine(m.topLine());
        builder.patternLine(m.midLine());
        builder.patternLine(m.botLine());

        StringBuilder inputStrings = new StringBuilder();
        m.getShapedInputs().stream().distinct().forEach((i) -> {
            inputStrings.append(i.getRegistryName().getPath().toLowerCase() + "_and_");
        });

        builder.build(consumer, toRL("crafting/" + inputStrings.toString() + "to_" + m.getOutput().getItem().getRegistryName().getPath().toLowerCase()));

    }

    private void addBloomeryMethod(IGProcessingMethod method, Consumer<IFinishedRecipe> consumer) {
        IGBloomeryProcessingMethod m = (IGBloomeryProcessingMethod) method;
        int fuelCost = m.getEnergyCost();
        int timeMult = m.getProcessingTime();

        ItemStack input = m.getInputItem();
        ItemStack output = m.getOutputItem();

        BloomeryRecipeBuilder.builder(output).setTime(timeMult).setEnergy(fuelCost).addItemInput(input).build(consumer, toRL("bloomery/" + input.getItem().getRegistryName().getPath().toLowerCase() + "_to_" + output.getItem().getRegistryName().getPath().toLowerCase()));
    }
    private void addCrystalizerMethod (IGProcessingMethod method, Consumer<IFinishedRecipe> consumer)
    {

        IGCrystalizerProcessingMethod crystalMethod = (IGCrystalizerProcessingMethod) method;
        int energyCost = crystalMethod.getEnergyCost();
        int processingTime = crystalMethod.getProcessingTime();
        ItemStack outputItem = crystalMethod.getOutputItem();
        FluidStack input_fluid = crystalMethod.getInputFluid();

        CrystalizerRecipeBuilder crystalizerRecipeBuilder = CrystalizerRecipeBuilder.builder(outputItem)
               .setEnergy(energyCost).setTime(processingTime);
        FluidTagInput input_chemical = new FluidTagInput(FluidTags.WATER, 1);

        if (input_fluid.getFluid() instanceof IGFluid)
        {
            IGFluid igFluid = (IGFluid) input_fluid.getFluid();
            MaterialFluidBase fluidWrapper = igFluid.getFluidMaterial();
            IGTags.MaterialTags tags = IGTags.getTagsFor(fluidWrapper);
            input_chemical = new FluidTagInput(tags.fluid, input_fluid.getAmount());
        }

        crystalizerRecipeBuilder.addFluidInput(input_chemical).build(consumer,toRL("crystalizer/" +
                 outputItem.getItem().getRegistryName().getPath().toLowerCase()));
    }

    private void addVatMethod (IGProcessingMethod method, Consumer<IFinishedRecipe> consumer)
    {
        VatRecipeBuilder vatRecipeBuilder = new VatRecipeBuilder();

        IGVatProcessingMethod m = (IGVatProcessingMethod) method;
        int energyCost = m.getEnergyCost();
        int timeTaken = m.getProcessingTime();

        FluidTagInput primary_chemical = new FluidTagInput(FluidTags.WATER, 1);
        FluidTagInput secondary_chemical = null;

        FluidStack output_chemical = m.getOutputFluid();

        FluidStack primary_input = m.getPrimaryInputFluid();
        if(!primary_input.isEmpty()){
            if(primary_input.getFluid() instanceof IGFluid) {
                IGFluid igFluid = (IGFluid) primary_input.getFluid();
                MaterialFluidBase fluidWrapper = igFluid.getFluidMaterial();
                IGTags.MaterialTags tags = IGTags.getTagsFor(fluidWrapper);
                primary_chemical = new FluidTagInput(tags.fluid, primary_input.getAmount());
            } else {
                primary_chemical = new FluidTagInput(primary_input.getFluid().isEquivalentTo(Fluids.WATER) ? FluidTags.WATER : FluidTags.LAVA, primary_input.getAmount());
            }
        }

        FluidStack secondary_input = m.getSecondaryInputFluid();
        if(!secondary_input.isEmpty()){
            if(secondary_input.getFluid() instanceof IGFluid) {
                IGFluid igFluid = (IGFluid) secondary_input.getFluid();
                MaterialFluidBase fluidWrapper = igFluid.getFluidMaterial();
                IGTags.MaterialTags tags = IGTags.getTagsFor(fluidWrapper);
                secondary_chemical = new FluidTagInput(tags.fluid, secondary_input.getAmount());
            }
        }
        ItemStack outputItem = m.getOutputItem();
        ItemStack itemInput = m.getInputItem() != null ? m.getInputItem() : ItemStack.EMPTY;

        log.info("Registering Chemical Vat Recipe");

        vatRecipeBuilder.builder(outputItem, output_chemical).addItemInput(itemInput).addFluidInputs(primary_chemical, secondary_chemical).setEnergy(energyCost).setTime(timeTaken)
                .build(consumer,toRL("chemicalvat/" + output_chemical.getFluid().getRegistryName().getPath().toLowerCase() +
                        "_" + outputItem.getItem().getRegistryName().getPath().toLowerCase()));

    }
    private void add3x3Conversion(IItemProvider bigItem, IItemProvider smallItem, ITag.INamedTag<Item> smallTag, Consumer<IFinishedRecipe> out)
    {
        ShapedRecipeBuilder.shapedRecipe(bigItem)
                .key('s', smallTag)
                .key('i', smallItem)
                .patternLine("sss")
                .patternLine("sis")
                .patternLine("sss")
                .addCriterion("has_"+toPath(smallItem), hasItem(smallItem))
                .build(out, toRL(toPath(smallItem)+"_to_")+toPath(bigItem));
        ShapelessRecipeBuilder.shapelessRecipe(smallItem, 9)
                .addIngredient(bigItem)
                .addCriterion("has_"+toPath(bigItem), hasItem(smallItem))
                .build(out, toRL(toPath(bigItem)+"_to_"+toPath(smallItem)));
    }

    private final int standardSmeltingTime = 200;
    private final int blastDivider = 2;

    public static ICondition getTagCondition(ITag.INamedTag<?> tag)
    {
        return new NotCondition(new TagEmptyCondition(tag.getName()));
    }

    public static ICondition getTagCondition(ResourceLocation tag)
    {
        return getTagCondition(createItemWrapper(tag));
    }

    private void addStandardSmeltingBlastingRecipe(IItemProvider input, IItemProvider output, float xp, Consumer<IFinishedRecipe> out)
    {
        addStandardSmeltingBlastingRecipe(input, output, xp, out, "");
    }

    private void addStandardSmeltingBlastingRecipe(IItemProvider input, IItemProvider output, float xp, Consumer<IFinishedRecipe> out, String extraPostfix)
    {
        addStandardSmeltingBlastingRecipe(input, output, xp, standardSmeltingTime, out, extraPostfix, false);
    }

    private void addStandardSmeltingBlastingRecipe(IItemProvider input, IItemProvider output, float xp, int smeltingTime, Consumer<IFinishedRecipe> out, String extraPostfix, boolean smeltPostfix)
    {
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(input), output, xp, smeltingTime).addCriterion("has_"+toPath(input), hasItem(input)).build(out, toRL(toPath(output)+extraPostfix+(smeltPostfix?"_from_smelting": "")));
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(input), output, xp, smeltingTime/blastDivider).addCriterion("has_"+toPath(input), hasItem(input)).build(out, toRL(toPath(output)+extraPostfix+"_from_blasting"));
    }

    private void addBlastingRecipe(IItemProvider input, IItemProvider output, float xp, int smeltingTime, Consumer<IFinishedRecipe> out){
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(input), output, xp, smeltingTime).addCriterion("has_"+toPath(input), hasItem(input)).build(out, toRL(toPath(output)+"_blasting"));
    }

    private String toPath(IItemProvider src)
    {
        return src.asItem().getRegistryName().getPath();
    }

    /**
     * Gets all the sub/offloaded recipe providers that this recipe provider has.
     *
     * @implNote This is only called once per provider so there is no need to bother caching the list that this returns
     */
    protected List<ISubRecipeProvider> getSubRecipeProviders() {
        return Collections.emptyList();
    }

    private ResourceLocation toRL(String s)
    {
        if(!s.contains("/"))
            s = "crafting/"+s;
        if(PATH_COUNT.containsKey(s))
        {
            int count = PATH_COUNT.get(s)+1;
            PATH_COUNT.put(s, count);
            return new ResourceLocation(IGLib.MODID, s+count);
        }
        PATH_COUNT.put(s, 1);
        return new ResourceLocation(IGLib.MODID, s);
    }
}
