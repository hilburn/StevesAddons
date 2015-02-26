package stevesaddons.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StevesAddonsTransformer implements IClassTransformer
{
    private enum MethodName
    {
        ACTIVATE_TRIGGER("activateTrigger", "(Lvswe/stevesfactory/components/FlowComponent;Ljava/util/EnumSet;)V", "vswe/stevesfactory/components/CommandExecutor", "vswe/stevesfactory/components/CommandExecutorRF"),
        GET_GUI("getGui", "(Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/entity/player/InventoryPlayer;)Lnet/minecraft/client/gui/GuiScreen;", "vswe/stevesfactory/interfaces/GuiManager", "stevesaddons/interfaces/GuiRFManager"),
        CREATE_TE("func_149915_a", "(Lnet/minecraft/world/World;I)Lnet/minecraft/tileentity/TileEntity;", "vswe/stevesfactory/blocks/TileEntityCluster", "vswe/stevesfactory/blocks/TileEntityRFCluster"),
        MANAGER_INIT("<init>", "()")
                {
                    @Override
                    public AbstractInsnNode getInjectionPoint(InsnList list)
                    {
                        AbstractInsnNode node = list.getLast();
                        while (!(node instanceof LineNumberNode && ((LineNumberNode)node).line == 85) && node != list.getFirst())
                            node = node.getPrevious();
                        return node;
                    }
                },
        ITEM_SETTING_LOAD("load", "(Lnet/minecraft/nbt/NBTTagCompound;)V")
                {
                    @Override
                    public AbstractInsnNode getInjectionPoint(InsnList list)
                    {
                        AbstractInsnNode node = list.getLast();
                        while (node.getOpcode() != Opcodes.RETURN && node != list.getFirst()) node = node.getPrevious();
                        return node;
                    }
                };

        private String name;
        private String args;
        public final String toReplace;
        public final String replace;
        public InsnList instructions = new InsnList();

        MethodName(String name, String args)
        {
            this(name, args, "", "");
        }

        MethodName(String name, String args, String toReplace, String replace)
        {
            this.name = name;
            this.args = args;
            this.replace = replace;
            this.toReplace = toReplace;
        }

        static
        {
            MANAGER_INIT.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            MANAGER_INIT.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "stevesaddons/asm/StevesHooks", "addCopyButton", "(Lvswe/stevesfactory/blocks/TileEntityManager;)V", false));

            ITEM_SETTING_LOAD.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            ITEM_SETTING_LOAD.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            ITEM_SETTING_LOAD.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "vswe/stevesfactory/components/ItemSetting", "item", "Lnet/minecraft/item/ItemStack;"));
            ITEM_SETTING_LOAD.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "stevesaddons/asm/StevesHooks", "fixLoadingStack", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", false));
            ITEM_SETTING_LOAD.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, "vswe/stevesfactory/components/ItemSetting", "item", "Lnet/minecraft/item/ItemStack;"));
        }

        public AbstractInsnNode getInjectionPoint(InsnList list)
        {
            return null;
        }

        public String getName()
        {
            return name;
        }

        public String getArgs()
        {
            return args;
        }
    }

    private enum ClassName
    {

        TE_MANAGER("vswe.stevesfactory.blocks.TileEntityManager", MethodName.ACTIVATE_TRIGGER, MethodName.GET_GUI, MethodName.MANAGER_INIT),
        RF_CLUSTER("vswe.stevesfactory.blocks.BlockCableCluster", MethodName.CREATE_TE),
        ITEM_SETTING_LOAD("vswe.stevesfactory.components.ItemSetting", MethodName.ITEM_SETTING_LOAD);
        private String name;
        private MethodName[] methods;


        ClassName(String name, MethodName... methods)
        {
            this.name = name;
            this.methods = methods;
        }

        public String getName()
        {
            return name;
        }

        public MethodName[] getMethods()
        {
            return methods;
        }
    }

    private static Map<String, ClassName> classMap = new HashMap<String, ClassName>();

    static
    {
        for (ClassName className : ClassName.values()) classMap.put(className.getName(), className);
    }

    @Override
    public byte[] transform(String className, String className2, byte[] bytes)
    {
        ClassName clazz = classMap.get(className);
        if (clazz != null)
        {
            for (MethodName method : clazz.getMethods())
            {
                bytes = method.instructions.size() > 0 ? inject(method, bytes) : replace(method, bytes);
            }
            classMap.remove(className);
        }

        return bytes;
    }

    private byte[] inject(MethodName methodName, byte[] data)
    {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        MethodNode methodNode = getMethodByName(classNode, methodName);
        AbstractInsnNode node = methodName.getInjectionPoint(methodNode.instructions);
        methodNode.instructions.insertBefore(node, methodName.instructions);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private byte[] replace(MethodName methodName, byte[] data)
    {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        MethodNode methodNode = getMethodByName(classNode, methodName);
        AbstractInsnNode node = methodNode.instructions.getFirst();


        do
        {
            if (node instanceof TypeInsnNode && ((TypeInsnNode)node).desc.equals(methodName.toReplace))
            {
                TypeInsnNode newNode = new TypeInsnNode(Opcodes.NEW, methodName.replace);
                methodNode.instructions.set(node, newNode);
                node = newNode;
            } else if (node instanceof MethodInsnNode && ((MethodInsnNode)node).owner.contains(methodName.toReplace))
            {
                MethodInsnNode newNode = new MethodInsnNode(node.getOpcode(), methodName.replace, ((MethodInsnNode)node).name, ((MethodInsnNode)node).desc, false);
                methodNode.instructions.set(node, newNode);
                node = newNode;
            }
            node = node.getNext();
        } while (node.getNext() != null);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public static MethodNode getMethodByName(ClassNode classNode, MethodName obfName)
    {
        List<MethodNode> methods = classNode.methods;
        for (int k = 0; k < methods.size(); k++)
        {
            MethodNode method = methods.get(k);
            if (method.name.equals(obfName.getName()) && method.desc.equals(obfName.getArgs()))
            {
                return method;
            }
        }
        return classNode.methods.get(0);
    }
}
