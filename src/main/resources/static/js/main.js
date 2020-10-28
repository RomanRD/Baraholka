document.addEventListener("DOMContentLoaded", () => {

    // ======== Slider with one handle

    const priceSlider = document.getElementById('r-slider');

    noUiSlider.create(priceSlider, {
        start: [10, 1990],
        tooltips: true,
        connect: true,
        padding: 6,
        range: {
            'min': 0,
            'max': 2000
        },
        pips: {
            mode: 'values',
            values: [50, 100, 150],
            density: 4
        }
    });

    priceSlider.noUiSlider.on('change', (values, handle) => {
        goSearch();
    });
    // ======== Search filters

    function goSearch() {
        let winHref = window.location.href.split('?')[0];
        winHref += `?pricerange=${priceSlider.noUiSlider.get()}`;
        winHref += `&mindiscount=${discountSlider.noUiSlider.get()}`;
        window.location.href = winHref;
    }


    // ======== Slider set

    const params = new URLSearchParams(window.location.search);
    const minDiscount = params.get("mindiscount");
    const priceRange = params.get("pricerange");

    if (minDiscount) {
        discountSlider.noUiSlider.set(parseInt(minDiscount));
    }
    if (priceRange) {
        priceSlider.noUiSlider.set(priceRange.split(','));
    }
});
