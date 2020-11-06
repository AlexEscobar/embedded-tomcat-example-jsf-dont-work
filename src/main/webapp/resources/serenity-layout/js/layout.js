/** 
 * PrimeFaces Serenity Layout
 */
PrimeFaces.widget.Serenity = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.wrapper = $(document.body).children('.layout-wrapper');

        var $this = this;
        $(function() {
            $this._init();
        });

        if(!this.wrapper.hasClass('layout-menu-horizontal')) {
            this.restoreMenuState();
        }
        
        this.expandedMenuitems = this.expandedMenuitems||[];
    },
    
    _init: function() {
        this.contentWrapper = this.wrapper.children('.layout-main');
        this.sidebar = this.wrapper.children('.layout-sidebar');
        this.menu = this.sidebar.find('.layout-menu');
        this.menulinks = this.menu.find('a');
        this.topbar = this.contentWrapper.find('.layout-topbar');
        this.menuButton = this.topbar.children('.menu-btn');
        this.topbarMenu = this.topbar.find('> .layout-topbar-menu-wrapper > .topbar-menu');
        this.topbarItems = this.topbarMenu.children('li');
        this.topbarLinks = this.topbarMenu.find('a');
        this.topbarMenuButton = this.topbar.find('> .topbar-menu-btn');
        this.anchorButton = this.sidebar.find('> .sidebar-logo > .sidebar-anchor');
        this.nano = this.sidebar.find('.nano');
        this.isRTL = this.wrapper.hasClass('layout-rtl');

        this.configButton = $('#layout-config-button');
        this.configMenu = $('#layout-config');
        this.configMenuClose = this.configMenu.find('> .layout-config-content > .layout-config-close');

        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
        
        $('.nano').nanoScroller({flash: true, isRTL: $this.isRTL});
        
        this.sidebar.off('mouseenter.sidebar mouseleave.sidebar').on('mouseenter.sidebar', function(e) {
            if(!$this.wrapper.hasClass('layout-sidebar-static')) {
                if($this.hideTimeout) {
                    clearTimeout($this.hideTimeout);
                }
                $this.sidebar.addClass('layout-sidebar-active');
            }
        })
        .on('mouseleave.sidebar', function(e) {
            if(!$this.wrapper.hasClass('layout-sidebar-static')) {
                $this.hideTimeout = setTimeout(function() {
                    $this.sidebar.removeClass('layout-sidebar-active');
                }, $this.cfg.closeDelay); 
            }
        });
        
        
        this.menulinks.off('click.menu').on('click.menu', function(e) {
            var link = $(this),
            item = link.parent(),
            submenu = item.children('ul');
            horizontal = $this.isHorizontal() && $this.isDesktop();
            
            if(horizontal) {
                $this.horizontalMenuClick = true;
            }
            
            if(item.hasClass('active-menuitem')) {
                if(submenu.length) {
                    $this.removeMenuitem(item.attr('id'));
                    item.removeClass('active-menuitem');
                    
                    if(horizontal) {
                        if(item.parent().is($this.jq)) {
                            $this.menuActive = false;
                        }
                        
                        submenu.hide();
                    }
                    else {
                        submenu.slideUp();
                    }
                    
                    submenu.find('.ink').remove();
                    
                    submenu.slideUp(function() {
                        $this.removeMenuitem(item.attr('id'));
                        item.removeClass('active-menuitem');
                    });
                }
            }
            else {
                $this.addMenuitem(item.attr('id'));

                if(horizontal) {
                    $this.deactivateItems(item.siblings());
                    item.addClass('active-menuitem');
                    $this.menuActive = true;
                    submenu.show();
                }
                else {
                    $this.deactivateItems(item.siblings(), true);
                    $this.activate(item);
                }
            }

            setTimeout(function() {
                $this.nano.nanoScroller();
            }, 500);
                                    
            if(!horizontal) {
                setTimeout(function() {
                    $(".nano").nanoScroller();
                }, 500);
            }
            
            if(submenu.length) {
                e.preventDefault();
            }
        });

        this.menu.find('> li').off('mouseenter.menu').on('mouseenter.menu', function(e) {    
            if($this.isHorizontal() && $this.isDesktop()) {
                var item = $(this);
                
                if(!item.hasClass('active-menuitem')) {
                    $this.menu.find('.active-menuitem').removeClass('active-menuitem');
                    $this.menu.find('ul:visible').hide();
                    $this.menu.find('.ink').remove();
                    
                    if($this.menuActive) {
                        item.addClass('active-menuitem');
                        item.children('ul').show();
                    }
                }
            }
        });

        this.topbarLinks.off('click.topbarLink').on('click.topbarLink',  function(e) {
            var link = $(this),
            item = link.parent(),
            submenu = link.next();
            
            $this.topbarClick = true;

            item.siblings('.active-topmenuitem').removeClass('active-topmenuitem'); 

            if($this.isMobile()) {
                item.children('ul').removeClass('fadeInDown fadeOutUp');
                item.toggleClass('active-topmenuitem');
            }
            else {
                if(submenu.length) {
                    if(item.hasClass('active-topmenuitem')) {
                        submenu.addClass('fadeOutUp').removeClass('fadeInDown');
                        
                        setTimeout(function() {
                            item.removeClass('active-topmenuitem'),
                            submenu.removeClass('fadeOutUp');
                        }, 450);
                    }
                    else {
                        item.addClass('active-topmenuitem');
                        submenu.addClass('fadeInDown');
                    }
                }
            }   
            
            var href = link.attr('href');
            if(href && href !== '#') {
                window.location.href = href;
            }
                        
            e.preventDefault();   
        });
        
        this.anchorButton.off('click.anchorButton').on('click.anchorButton', function(e) {
            $this.wrapper.removeClass('layout-wrapper-static-restore');
            $this.wrapper.toggleClass('layout-wrapper-static');
            $this.saveMenuState();
            e.preventDefault();
        });
        
        this.menuButton.off('click.menubutton').on('click.menubutton', function(e) {
            $this.wrapper.removeClass('layout-wrapper-static-restore').toggleClass('layout-wrapper-active');
            $(document.body).toggleClass('hidden-overflow');
            
            setTimeout(function() {
                $this.nano.nanoScroller();
            }, 500);
            
            e.preventDefault();
        });
        
        this.topbarMenuButton.off('click.topbar').on('click.topbar', function(e) {
            $this.topbarClick = true;
            $this.topbarMenu.find('ul').removeClass('fadeInDown fadeOutUp');

            if($this.topbarMenu.hasClass('topbar-menu-active')) {
                $this.topbarMenu.addClass('fadeOutUp').removeClass('fadeInDown');
                
                setTimeout(function() {
                    $this.topbarMenu.removeClass('topbar-menu-active fadeOutUp');
                }, 450);
            }
            else {
                $this.topbarMenu.addClass('topbar-menu-active fadeInDown');
            }
                        
            e.preventDefault();
        });
        
        this.topbarItems.filter('.search-item').off('click.topbar').on('click.topbar', function() {
            $this.topbarClick = true;
        });
        
        this.contentWrapper.children('.layout-main-mask').off('click.mask').on('click.mask', function(e) {
            $this.wrapper.removeClass('layout-wrapper-active layout-wrapper-static-restore');
            $(document.body).removeClass('hidden-overflow');
        });

        this.bindConfigEvents();
        
        $(document.body).off('click.layoutBody').on('click.layoutBody', function() {
            if($this.isHorizontal() && !$this.horizontalMenuClick && $this.isDesktop()) {
                $this.menu.find('.active-menuitem').removeClass('active-menuitem');
                $this.menu.find('ul:visible').hide();
                $this.menuActive = false;
            }

            if(!$this.topbarClick) {
                $this.topbarItems.filter('.active-topmenuitem').removeClass('active-topmenuitem');
                $this.topbarMenu.removeClass('topbar-menu-active');
            }
                    
            if (!$this.configMenuClicked) {
                $this.configMenu.removeClass('layout-config-active');
            }

            $this.horizontalMenuClick = false;
            $this.topbarClick = false;
            $this.configMenuClicked = false;
        });
    },


    bindConfigEvents: function() {
        var $this = this;
        var changeConfigMenuState = function(e) {
            this.toggleClass(this.configMenu, 'layout-config-active');
            
            this.configMenuClicked = true;
            e.preventDefault();
        };
        
        this.configButton.off('click.config').on('click.config', changeConfigMenuState.bind(this));
        this.configMenuClose.off('click.config').on('click.config', changeConfigMenuState.bind(this));
        
        this.configMenu.off('click.config').on('click.config', function() {
            $this.configMenuClicked = true;
        });
    },
        
    activate: function(item) {
        var submenu = item.children('ul');
        item.addClass('active-menuitem');

        if(submenu.length) {
            submenu.slideDown();
        }
    },
    
    deactivate: function(item) {
        var submenu = item.children('ul');
        item.removeClass('active-menuitem').find('.ink').remove();
        
        if(submenu.length) {
            submenu.hide();
            submenu.find('.ink').remove();
        }
    },
        
    deactivateItems: function(items, animate) {
        var $this = this;
        
        for(var i = 0; i < items.length; i++) {
            var item = items.eq(i),
            submenu = item.children('ul');
            
            if(submenu.length) {
                if(item.hasClass('active-menuitem')) {
                    var activeSubItems = item.find('.active-menuitem');
                    item.removeClass('active-menuitem');
                    
                    if(animate) {
                        submenu.slideUp('normal', function() {
                            $(this).parent().find('.active-menuitem').each(function() {
                                $this.deactivate($(this));
                            });
                        });
                    }
                    else {
                        submenu.hide();
                        item.find('.active-menuitem').each(function() {
                            $this.deactivate($(this));
                        });
                    }
                    
                    $this.removeMenuitem(item.attr('id'));
                    activeSubItems.each(function() {
                        $this.removeMenuitem($(this).attr('id'));
                    });
                }
                else {
                    item.find('.active-menuitem').each(function() {
                        var subItem = $(this);
                        $this.deactivate(subItem);
                        $this.removeMenuitem(subItem.attr('id'));
                    });
                }
            }
            else if(item.hasClass('active-menuitem')) {
                $this.deactivate(item);
                $this.removeMenuitem(item.attr('id'));
            }
        }
    },
    
    removeMenuitem: function (id) {
        this.expandedMenuitems = $.grep(this.expandedMenuitems, function (value) {
            return value !== id;
        });
        this.saveMenuState();
    },
    
    addMenuitem: function (id) {
        if ($.inArray(id, this.expandedMenuitems) === -1) {
            this.expandedMenuitems.push(id);
        }
        this.saveMenuState();
    },
    
    saveMenuState: function() {
        if(this.isHorizontal()) {
            return;
        }
        
        if(this.wrapper.hasClass('layout-wrapper-static'))
            $.cookie('serenity_menu_static', 'serenity_menu_static', {path: '/'});
        else
            $.removeCookie('serenity_menu_static', {path: '/'});
        
        $.cookie('serenity_expandeditems', this.expandedMenuitems.join(','), {path: '/'});
    },
    
    clearMenuState: function() {
        this.expandedMenuitems = [];
        $.removeCookie('serenity_expandeditems', {path: '/'});
        $.removeCookie('serenity_menu_static', {path: '/'});
    },
    
    restoreMenuState: function() {
        var menuCookie = $.cookie('serenity_expandeditems');
        if (menuCookie) {
            this.expandedMenuitems = menuCookie.split(',');
            for (var i = 0; i < this.expandedMenuitems.length; i++) {
                var id = this.expandedMenuitems[i];
                if (id) {
                    var menuitem = $("#" + this.expandedMenuitems[i].replace(/:/g, "\\:"));
                    menuitem.addClass('active-menuitem');
                    
                    var submenu = menuitem.children('ul');
                    if(submenu.length) {
                        submenu.show();
                    }
                }
            }
        }
        
        var sidebarCookie = $.cookie('serenity_menu_static');
        if(sidebarCookie) {
            this.wrapper.addClass('layout-wrapper-static layout-wrapper-static-restore');
        }
    },

    clearLayoutState: function() {
        this.clearMenuState();
        this.clearActiveItems();
    },

    clearActiveItems: function() {
        var activeItems = this.jq.find('li.active-menuitem'),
        subContainers = activeItems.children('ul');

        activeItems.removeClass('active-menuitem').find('.ink').remove();
        if(subContainers && subContainers.length) {
            subContainers.hide();
            subContainers.find('.ink').remove();
        }
    },
    
    hideTopBar: function() {
        var $this = this;
        this.topbarMenu.addClass('fadeOutUp');
        
        setTimeout(function() {
            $this.topbarMenu.removeClass('fadeOutUp topbar-menu-visible');
        },500);
    },
    
    isMobile: function() {
        return window.innerWidth <= 640;
    },

    isHorizontal: function() {
        return this.wrapper.hasClass('layout-menu-horizontal');
    },

    isDesktop: function() {
        return window.innerWidth > 1024;
    },

    toggleClass: function(el, className) {
        if (el.hasClass(className)) {
            el.removeClass(className);
        }
        else {
            el.addClass(className);
        }
    }
});

PrimeFaces.SerenityConfigurator = {
    changeLayout: function(layoutTheme) {
        var linkElement = $('link[href*="layout-"]');
        var href = linkElement.attr('href');
        var startIndexOf = href.indexOf('layout-') + 7;
        var endIndexOf = href.indexOf('.css');
        var currentColor = href.substring(startIndexOf, endIndexOf);

        this.replaceLink(linkElement, href.replace(currentColor, layoutTheme));
    },
    
    changeComponentsTheme: function(theme) {
        var library = 'primefaces-serenity';
        var linkElement = $('link[href*="theme.css"]');
        var href = linkElement.attr('href');
        var index = href.indexOf(library) + 1;
        var currentTheme = href.substring(index + library.length);

        this.replaceLink(linkElement, href.replace(currentTheme, theme));
    },

    beforeResourceChange: function() {
        PrimeFaces.ajax.RESOURCE = null;    //prevent resource append
    },
    
    replaceLink: function(linkElement, href) {
        PrimeFaces.ajax.RESOURCE = 'javax.faces.Resource';

        var isIE = this.isIE();

        if (isIE) {
            linkElement.attr('href', href);
        }
        else {
            var cloneLinkElement = linkElement.clone(false);

            cloneLinkElement.attr('href', href);
            linkElement.after(cloneLinkElement);
            
            cloneLinkElement.off('load').on('load', function() {
                linkElement.remove();
            });
        }
    },

    changeMenuToHorizontal: function() {
        this.clearLayoutState();
        $('.layout-wrapper').addClass('layout-menu-horizontal');
    },

    changeMenuToVertical: function() {
        this.clearLayoutState();
        $('.layout-wrapper').removeClass('layout-menu-horizontal');
    },

    changeMenuToDark: function() {
        $('.layout-sidebar').addClass('layout-sidebar-dark ');
    },

    changeMenuToLight: function() {
        $('.layout-sidebar').removeClass('layout-sidebar-dark');
    },

    changeMenuToLTR: function() {
        $('.layout-wrapper').removeClass('layout-rtl');
        this.updateNano(false);
    },

    changeMenuToRTL: function() {
        $('.layout-wrapper').addClass('layout-rtl');
        this.updateNano(true);
    },
    
    updateNano: function(isRTL) {
        var nanoPanel = $('.layout-wrapper').find('.nano');
        nanoPanel.nanoScroller({destroy: true});
        nanoPanel.nanoScroller({isRTL: isRTL});
    },

    clearLayoutState: function() {
        var menu = PF('serenityMenuWidget');

        if (menu) {
            menu.clearLayoutState();
        }
    },
    
    isIE: function() {
        return /(MSIE|Trident\/|Edge\/)/i.test(navigator.userAgent);
    }
};

/*!
 * jQuery Cookie Plugin v1.4.1
 * https://github.com/carhartl/jquery-cookie
 *
 * Copyright 2006, 2014 Klaus Hartl
 * Released under the MIT license
 */
(function (factory) {
	if (typeof define === 'function' && define.amd) {
		// AMD (Register as an anonymous module)
		define(['jquery'], factory);
	} else if (typeof exports === 'object') {
		// Node/CommonJS
		module.exports = factory(require('jquery'));
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function ($) {

	var pluses = /\+/g;

	function encode(s) {
		return config.raw ? s : encodeURIComponent(s);
	}

	function decode(s) {
		return config.raw ? s : decodeURIComponent(s);
	}

	function stringifyCookieValue(value) {
		return encode(config.json ? JSON.stringify(value) : String(value));
	}

	function parseCookieValue(s) {
		if (s.indexOf('"') === 0) {
			// This is a quoted cookie as according to RFC2068, unescape...
			s = s.slice(1, -1).replace(/\\"/g, '"').replace(/\\\\/g, '\\');
		}

		try {
			// Replace server-side written pluses with spaces.
			// If we can't decode the cookie, ignore it, it's unusable.
			// If we can't parse the cookie, ignore it, it's unusable.
			s = decodeURIComponent(s.replace(pluses, ' '));
			return config.json ? JSON.parse(s) : s;
		} catch(e) {}
	}

	function read(s, converter) {
		var value = config.raw ? s : parseCookieValue(s);
		return $.isFunction(converter) ? converter(value) : value;
	}

	var config = $.cookie = function (key, value, options) {

		// Write

		if (arguments.length > 1 && !$.isFunction(value)) {
			options = $.extend({}, config.defaults, options);

			if (typeof options.expires === 'number') {
				var days = options.expires, t = options.expires = new Date();
				t.setMilliseconds(t.getMilliseconds() + days * 864e+5);
			}

			return (document.cookie = [
				encode(key), '=', stringifyCookieValue(value),
				options.expires ? '; expires=' + options.expires.toUTCString() : '', // use expires attribute, max-age is not supported by IE
				options.path    ? '; path=' + options.path : '',
				options.domain  ? '; domain=' + options.domain : '',
				options.secure  ? '; secure' : ''
			].join(''));
		}

		// Read

		var result = key ? undefined : {},
			// To prevent the for loop in the first place assign an empty array
			// in case there are no cookies at all. Also prevents odd result when
			// calling $.cookie().
			cookies = document.cookie ? document.cookie.split('; ') : [],
			i = 0,
			l = cookies.length;

		for (; i < l; i++) {
			var parts = cookies[i].split('='),
				name = decode(parts.shift()),
				cookie = parts.join('=');

			if (key === name) {
				// If second argument (value) is a function it's a converter...
				result = read(cookie, value);
				break;
			}

			// Prevent storing a cookie that we couldn't decode.
			if (!key && (cookie = read(cookie)) !== undefined) {
				result[name] = cookie;
			}
		}

		return result;
	};

	config.defaults = {};

	$.removeCookie = function (key, options) {
		// Must not alter options, thus extending a fresh object...
		$.cookie(key, '', $.extend({}, options, { expires: -1 }));
		return !$.cookie(key);
	};

}));

/* JS extensions to support material animations */
if(PrimeFaces.widget.InputSwitch) {
    PrimeFaces.widget.InputSwitch = PrimeFaces.widget.InputSwitch.extend({
         
         init: function(cfg) {
             this._super(cfg);
             
             if(this.input.prop('checked')) {
                 this.jq.addClass('ui-inputswitch-checked');
             }
         },
         
         toggle: function() {
             var $this = this;

             if(this.input.prop('checked')) {
                 this.uncheck(); 
                 setTimeout(function() {
                    $this.jq.removeClass('ui-inputswitch-checked');
                 }, 100);
             }
             else {
                 this.check();
                 setTimeout(function() {
                    $this.jq.addClass('ui-inputswitch-checked');
                 }, 100);
             }
         }
    });
}

if(PrimeFaces.widget.SelectBooleanButton) {
    PrimeFaces.widget.SelectBooleanButton.prototype.check = function() {
        if(!this.disabled) {
            this.input.prop('checked', true);
            this.jq.addClass('ui-state-active').children('.ui-button-text').contents()[0].textContent = this.cfg.onLabel;

            if(this.icon.length > 0) {
                this.icon.removeClass(this.cfg.offIcon).addClass(this.cfg.onIcon);
            }

            this.input.trigger('change');
        }
    }
    
    PrimeFaces.widget.SelectBooleanButton.prototype.uncheck = function() {
        if(!this.disabled) {
            this.input.prop('checked', false);
            this.jq.removeClass('ui-state-active').children('.ui-button-text').contents()[0].textContent = this.cfg.offLabel;

            if(this.icon.length > 0) {
                this.icon.removeClass(this.cfg.onIcon).addClass(this.cfg.offIcon);
            }

            this.input.trigger('change');
        }
    }
}

PrimeFaces.skinInput = function(input) {
    setTimeout(function() {
        if(input.val() != '') {
            var parent = input.parent();
            input.addClass('ui-state-filled');
            
            if(parent.is("span:not('.md-inputfield')")) {
                parent.addClass('md-inputwrapper-filled');
            }
        }
    }, 1);
    
    input.on('mouseenter', function() {
        $(this).addClass('ui-state-hover');
    })
    .on('mouseleave', function() {
        $(this).removeClass('ui-state-hover');
    })
    .on('focus', function() {
        var parent = input.parent();
        $(this).addClass('ui-state-focus');
        
        if(parent.is("span:not('.md-inputfield')")) {
            parent.addClass('md-inputwrapper-focus');
        }
    })
    .on('blur', function() {
        $(this).removeClass('ui-state-focus');

        if(input.hasClass('hasDatepicker')) {
            setTimeout(function() {
                PrimeFaces.onInputBlur(input);
            },150);
        }
        else {
            PrimeFaces.onInputBlur(input);
        }
    });

    //aria
    input.attr('role', 'textbox')
            .attr('aria-disabled', input.is(':disabled'))
            .attr('aria-readonly', input.prop('readonly'));

    if(input.is('textarea')) {
        input.attr('aria-multiline', true);
    }

    return this;
};

PrimeFaces.onInputBlur = function(input) {
    var parent = input.parent(),
    hasInputFieldClass = parent.is("span:not('.md-inputfield')");
    
    if(parent.hasClass('md-inputwrapper-focus')) {
        parent.removeClass('md-inputwrapper-focus');
    }
    
    if(input.val() != '') {
        input.addClass('ui-state-filled');
        if(hasInputFieldClass) {
            parent.addClass('md-inputwrapper-filled');
        }
    }
    else {
        input.removeClass('ui-state-filled');
        parent.removeClass('md-inputwrapper-filled');
    }    
};

if(PrimeFaces.widget.AutoComplete) {
    PrimeFaces.widget.AutoComplete.prototype.setupMultipleMode = function() {
        var $this = this;
        this.multiItemContainer = this.jq.children('ul');
        this.inputContainer = this.multiItemContainer.children('.ui-autocomplete-input-token');

        this.multiItemContainer.hover(function() {
                $(this).addClass('ui-state-hover');
            },
            function() {
                $(this).removeClass('ui-state-hover');
            }
        ).click(function() {
            $this.input.focus();
        });

        //delegate events to container
        this.input.focus(function() {
            $this.multiItemContainer.addClass('ui-state-focus');
            $this.jq.addClass('md-inputwrapper-focus');
        }).blur(function(e) {
            $this.multiItemContainer.removeClass('ui-state-focus');
            $this.jq.removeClass('md-inputwrapper-focus').addClass('md-inputwrapper-filled');
            
            setTimeout(function() {
                if($this.hinput.children().length == 0 && !$this.multiItemContainer.hasClass('ui-state-focus')) {
                    $this.jq.removeClass('md-inputwrapper-filled');
                }
            }, 150); 
        });

        var closeSelector = '> li.ui-autocomplete-token > .ui-autocomplete-token-icon';
        this.multiItemContainer.off('click', closeSelector).on('click', closeSelector, null, function(event) {
            if($this.multiItemContainer.children('li.ui-autocomplete-token').length === $this.cfg.selectLimit) {
                if(PrimeFaces.isIE(8)) {
                    $this.input.val('');
                }
                $this.input.css('display', 'inline');
                $this.enableDropdown();
            }
            $this.removeItem(event, $(this).parent());
        });
    };
};

if(PrimeFaces.widget.Calendar) {
    PrimeFaces.widget.Calendar.prototype.bindDateSelectListener = function() {
        var _self = this;

        this.cfg.onSelect = function() {
            if(_self.cfg.popup) {
                _self.fireDateSelectEvent();
            }
            else {
                var newDate = $.datepicker.formatDate(_self.cfg.dateFormat, _self.getDate());

                _self.input.val(newDate);
                _self.fireDateSelectEvent();
            }
            
            if(_self.input.val() != '') {
               var parent = _self.input.parent();
               parent.addClass('md-inputwrapper-filled');
               _self.input.addClass('ui-state-filled');
           }
        };
    };
}

/* Issue #924 is fixed for 5.3+ and 6.0. (compatibility with 5.3) */
if(window['PrimeFaces'] && window['PrimeFaces'].widget.Dialog) {
    PrimeFaces.widget.Dialog = PrimeFaces.widget.Dialog.extend({
        
        enableModality: function() {
            this._super();
            $(document.body).children(this.jqId + '_modal').addClass('ui-dialog-mask');
        },
        
        syncWindowResize: function() {}
    });
}

/* Issue #2131 */
if(window['PrimeFaces'] && window['PrimeFaces'].widget.Schedule && isLtPF8Version()) {
    PrimeFaces.widget.Schedule = PrimeFaces.widget.Schedule.extend({
        
        setupEventSource: function() {
            var $this = this,
            offset = moment().utcOffset()*60000;

            this.cfg.events = function(start, end, timezone, callback) {
                var options = {
                    source: $this.id,
                    process: $this.id,
                    update: $this.id,
                    formId: $this.cfg.formId,
                    params: [
                        {name: $this.id + '_start', value: start.valueOf() + offset},
                        {name: $this.id + '_end', value: end.valueOf() + offset}
                    ],
                    onsuccess: function(responseXML, status, xhr) {
                        PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                                widget: $this,
                                handle: function(content) {
                                    callback($.parseJSON(content).events);
                                }
                            });

                        return true;
                    }
                };

                PrimeFaces.ajax.Request.handle(options);
            }
        }
    });
}

if(PrimeFaces.widget.SelectOneMenu) {
    PrimeFaces.widget.SelectOneMenu = PrimeFaces.widget.SelectOneMenu.extend({
        init: function(cfg) {
            this._super(cfg);

            var $this = this;
            if(this.jq.parent().hasClass('md-inputfield')) {
                this.itemsContainer.children('.ui-selectonemenu-item:first').css({'display': 'none'});
                if (this.input.val() != "") {
                    this.jq.addClass("ui-state-filled");
                }

                this.input.off('change').on('change', function() {
                    $this.inputValueControl($(this));
                });
                
                if(this.cfg.editable) {
                    this.label.on('input', function(e) {
                        $this.inputValueControl($(this));
                    }).on('focus', function() {
                        $this.jq.addClass('ui-state-focus');
                    }).on('blur', function() {
                        $this.jq.removeClass('ui-state-focus');
                        $this.inputValueControl($(this));
                    });
                }
            }
        },
        
        inputValueControl: function(input) {
            if (input.val() != "")
                this.jq.addClass("ui-state-filled"); 
            else
                this.jq.removeClass("ui-state-filled");
        },

        show: function() {
            this.jq.addClass('ui-overlay-panel-visible');
            this._super();
        },

        hide: function() {
            this.jq.removeClass('ui-overlay-panel-visible');
            this._super();
        }
    });
}

function isLtPF8Version() {
    var version = window['PrimeFaces'].VERSION;
    if (!version) {
        return true;
    }

    return parseInt(version.split('.')[0], 10) < 8;
}