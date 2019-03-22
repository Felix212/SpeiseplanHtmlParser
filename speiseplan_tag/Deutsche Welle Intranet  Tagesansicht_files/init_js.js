jQuery('#myTab a[href="#nachrichten"]').click(function (e) {
		e.preventDefault()
		jQuery(this).tab('show')
});
jQuery('#myTab a[href="#abteilungen"]').click(function (e) {
		e.preventDefault()
		jQuery(this).tab('show')
});

$(document).ready(function() {
	/* Apply fancybox to multiple items */
	$("a.fancybox").fancybox({
		openEffect	:	'elastic',
		closeEffect	:	'elastic',
		openSpeed		:	100,
		closeSpeed		:	100,
		fitToView	: true,
		helpers: {
				title: null
		}
	});
	/* Apply fancybox in iFrame */
	$(".mitarbeitertabelle a.fancybox").fancybox({
		openEffect	:	'elastic',
		closeEffect	:	'elastic',
		openSpeed		:	100,
		closeSpeed		:	100,
		fitToView	: true,
		type: 'iframe',
		helpers: {
				title: null
		}
	});
	$("a.fancybox-flowpayer").fancybox({
		openEffect	:	'elastic',
		closeEffect	:	'elastic',
		openSpeed		:	100,
		closeSpeed		:	100,
		width	: '800',
		height	: '500',
		autoSize	:false,
		fitToView	: false,
		scrolling	: 'no',
		helpers: {
				title: null
		}
	});
});