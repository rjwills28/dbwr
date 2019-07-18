
function handleActionButtonClick(widget)
{    
    // XXX Only handles first action (-0)
    
    let pv  = widget.data("pv-0");
    let val = widget.data("value-0");
    if (pv !== undefined  &&  val !== undefined)
    {
        console.log("Action button writes " + pv + " = " + val);
        dbwr.write(pv, val);
        return;
    }
    
    // Open web page or display?
    let new_link = widget.data("linked-file-0");
    if (new_link)
    {
        new_link = window.location.origin + window.location.pathname + "?display=" + escape(new_link);
        // alert("Button opens " + new_link);
        
        // Check for macros
        macros = widget.data("linked-macros-0");
        if (macros)
            new_link += "&macros=" + encodeURIComponent(widget.data("linked-macros-0"));
        // alert("Link with macros: " + new_link);
    }
    else
        new_link = widget.data("linked-url-0");
    
    if (new_link)
        window.location.href = new_link;
    else
    {
        console.log("ActionButton with unknown action");
        console.log(widget.data());
        return;
    }
}

DisplayBuilderWebRuntime.prototype.widget_init_methods['action_button'] = function(widget)
{
    widget.click(() => handleActionButtonClick(widget));
    
    // Subscribe to all data-pv-#
    let i = 0;
    let pv_name = widget.data("pv-" + i);
    while (pv_name !== undefined)
    {
        console.log("Button subscribes to " + pv_name);
        dbwr.subscribe(widget, 'action_button', pv_name);
        ++i;
        pv_name = widget.data("pv-" + i);
    }
}


DisplayBuilderWebRuntime.prototype.widget_update_methods['action_button'] = function(widget, data)
{
    // TODO Alarm border?

    if (data.readonly)
        widget.css("cursor", "not-allowed");
    else
        widget.css("cursor", "pointer");
    widget.prop('disabled', data.readonly);
}

