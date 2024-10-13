package me.kalmemarq.render;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Callback;

import java.io.Closeable;
import java.util.Optional;

public class ImGuiLayer implements Closeable {
    private final Window window;
    private ImGuiImplGlfw imGuiGlfw;
    private ImGuiImplGl3 imGuiGl3;

    public ImGuiLayer(Window window) {
        this.window = window;
    }

    public void init() {
        this.imGuiGlfw = new ImGuiImplGlfw();
        this.imGuiGl3 = new ImGuiImplGl3();

        ImGui.createContext();
        this.imGuiGlfw.init(this.window.getHandle(), false);
        Optional.ofNullable(GLFW.glfwSetMonitorCallback(null)).ifPresent(Callback::free);
        this.imGuiGlfw.installCallbacks(this.window.getHandle());
        this.imGuiGl3.init("#version 330");
    }

    public void startFrame() {
        if (this.imGuiGlfw == null) {
            this.init();
        }

        this.imGuiGl3.newFrame();
        this.imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public void endFrame() {
        ImGui.render();
        this.imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    @Override
    public void close() {
        if (this.imGuiGlfw != null) {
            this.imGuiGlfw.shutdown();
            this.imGuiGl3.shutdown();
            ImGui.destroyContext();
        }
    }
}
