function block=tkscaleblk(block,flag)
    if flag == 1 then
        // Output update
        slider = get(block.uid + "#slider");

        if slider <> [] then
            // calculate real value
            value = get(slider,"value") / block.rpar(3);

            w = get(block.uid);
            if w <> [] then
                set(w, "info_message", string(value));
            end

            block.outptr(1) = value;
        end
    elseif flag == 4 then
        // Initialization

        // if already exists (stopped) then reuse
        f = get(block.uid);
        if f <> [] then
            return;
        end

        f = figure("Figure_name", "TK Source: " + block.label, ...
        "dockable", "off", ...
        "infobar_visible" , "on", ...
        "toolbar", "none", ...
        "menubar_visible", "off", ...
        "menubar", "none", ...
        "backgroundcolor", [1 1 1], ...
        "default_axes", "off", ...
        "figure_size", [180 350], ...
        "layout", "border", ...
        "figure_position", [40 40], ...
        "Tag", block.uid);

        frame_slider = uicontrol(f, ...
        "style", "frame", ...
        "constraints", createConstraints("border", "left", [180, 0]), ...
        "border", createBorder("line", "lightGray", 1), ...
        "backgroundcolor", [1 1 1], ...
        "layout", "gridbag");

        // slider
        bounds = block.rpar(1:2);
        initial = mean(bounds);
        uicontrol(frame_slider, ...
        "Style", "slider", ...
        "Tag", block.uid + "#slider", ...
        "Min", bounds(1), ...
        "Max", bounds(2), ...
        "Value", initial, ...
        "Position", [0 0 10 20], ...
        "SliderStep", [block.rpar(3) 2*block.rpar(3)]);

        frame_label = uicontrol(frame_slider, ...
        "style", "frame", ...
        "constraints", createConstraints("border", "right"), ...
        "backgroundcolor", [1 1 1], ...
        "layout", "gridbag");

        // labels
        labels = string([bounds(2) ; ...
        mean([bounds(2) initial])  ; ...
        initial                    ; ...
        mean([bounds(1) initial])  ; ...
        bounds(1)]);
        labels = "<html>" + strcat(labels, "<br /><br /><br />") + "</html>";

        uicontrol(frame_label, ...
        "Style", "text", ...
        "String", labels(1), ...
        "FontWeight", "bold", ...
        "backgroundcolor", [1 1 1]);

        // update default value
        block.outptr(1) = initial / block.rpar(3);
    elseif flag == 5 then
        // Ending
        f = get(block.uid);
        if f <> [] then
            close(f);
        end
    end
endfunction