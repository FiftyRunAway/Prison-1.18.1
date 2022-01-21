package org.runaway.items.parameters;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.runaway.Prison;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class ParameterMeta {
    private Map<Parameter, Object> parametersMap;

    public ParameterMeta() {
        this.parametersMap = new HashMap();
    }

    public ParameterMeta(Map<Parameter, Object> parametersMap) {
        this.parametersMap = parametersMap;
    }

    public ParameterMeta(ItemStack itemStack) {
        getAllParameters(itemStack);
    }

    public ParameterMeta getAllParameters(ItemStack itemStack) {
        PrisonItem prisonItem = ItemManager.getPrisonItem(itemStack);
        List<Parameter> mutableParameters = prisonItem.getMutableParameters();
        Map<Parameter, Object> result = new HashMap<>();
        mutableParameters.forEach(parameter -> {
            result.put(parameter, parameter.getParameterGetter().apply(itemStack, null));
        });
        setParametersMap(result);
        return this;
    }

    public ItemStack applyTo(ItemStack itemStack) {
        parametersMap.forEach((parameter, o) -> {
            parameter.changeValues(itemStack, o);
        });
        return itemStack;
    }
}
