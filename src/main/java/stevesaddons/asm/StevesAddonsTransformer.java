package stevesaddons.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StevesAddonsTransformer implements IClassTransformer
{
    private enum MethodName
    {
        ACTIVATE_TRIGGER("activateTrigger","(Lvswe/stevesfactory/components/FlowComponent;Ljava/util/EnumSet;)V", "vswe/stevesfactory/components/CommandExecutor", "vswe/stevesfactory/components/CommandExecutorRF"),
        GET_GUI("getGui","(Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/entity/player/InventoryPlayer;)Lnet/minecraft/client/gui/GuiScreen;","vswe/stevesfactory/interfaces/GuiManager","stevesaddons/interfaces/GuiRFManager"),
        CREATE_TE("func_149915_a","(Lnet/minecraft/world/World;I)Lnet/minecraft/tileentity/TileEntity;","vswe/stevesfactory/blocks/TileEntityCluster","vswe/stevesfactory/blocks/TileEntityRFCluster"),
        MANAGER_INIT("<init>","()");

        private String deObf;
        private String obf;
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
            deObf = name;
            this.args = args;
            this.replace = replace;
            this.toReplace = toReplace;
        }

        static
        {
            MANAGER_INIT.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            MANAGER_INIT.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "stevesaddons/asm/StevesHooks", "addCopyButton", "(Lvswe/stevesfactory/blocks/TileEntityManager;)V", false));
        }

        public String getName()
        {
            if (obf==null || !LoadingPlugin.runtimeDeobfEnabled) return deObf;
            return obf;
        }

        public String getArgs()
        {
            return args;
        }
    }

    private enum ClassName
    {

        TE_MANAGER("vswe.stevesfactory.blocks.TileEntityManager",MethodName.ACTIVATE_TRIGGER,MethodName.GET_GUI,MethodName.MANAGER_INIT),
        RF_CLUSTER("vswe.stevesfactory.blocks.BlockCableCluster",MethodName.CREATE_TE);
        private String deObf;
        private String obf;
        private MethodName[] methods;


        ClassName(String name, MethodName... methods)
        {
            deObf = name;
            this.methods = methods;
        }

        public String getName()
        {
            if (obf==null || !LoadingPlugin.runtimeDeobfEnabled) return deObf;
            return obf;
        }

        public MethodName[] getMethods()
        {
            return methods;
        }
    }

    private static Map<String,ClassName> classMap = new HashMap<String, ClassName>();

    static
    {
        for (ClassName className: ClassName.values()) classMap.put(className.getName(),className);
    }

    @Override
    public byte[] transform(String className, String className2, byte[] bytes)
    {
        ClassName clazz = classMap.get(className);
        if (clazz!=null)
        {
            for (MethodName method: clazz.getMethods())
            {
                bytes = method.instructions.size()>0?inject(method,bytes):replace(method, bytes);
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
        AbstractInsnNode node = methodNode.instructions.getLast();
        while (!(node instanceof LineNumberNode && ((LineNumberNode)node).line == 85)) node = node.getPrevious();
        methodNode.instructions.insertBefore(node,methodName.instructions);
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
                TypeInsnNode newNode = new TypeInsnNode(Opcodes.NEW,methodName.replace);
                methodNode.instructions.set(node, newNode);
                node = newNode;
            } else if (node instanceof MethodInsnNode && ((MethodInsnNode)node).owner.contains(methodName.toReplace))
            {
                MethodInsnNode newNode = new MethodInsnNode(node.getOpcode(), methodName.replace, ((MethodInsnNode)node).name,((MethodInsnNode)node).desc,false);
                methodNode.instructions.set(node, newNode);
                node = newNode;
            }
            node = node.getNext();
        }while (node.getNext()!=null);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private static AbstractInsnNode findMethodNode(MethodName name, MethodNode methodNode)
    {
        for (Iterator<AbstractInsnNode> itr = methodNode.instructions.iterator(); itr.hasNext();)
        {
            AbstractInsnNode node = itr.next();
            if (node instanceof MethodInsnNode)
            {
                if (((MethodInsnNode)node).name.equals(name.getName())) return node;
            }
        }
        return methodNode.instructions.getLast();
    }

    public static MethodNode getMethodByName(ClassNode classNode, MethodName obfName) {
        List<MethodNode> methods = classNode.methods;
        for (int k = 0; k < methods.size(); k++) {
            MethodNode method = methods.get(k);
            if (method.name.equals(obfName.getName()) && method.desc.equals(obfName.getArgs())) {
                return method;
            }
        }
        return classNode.methods.get(0);
    }
}
