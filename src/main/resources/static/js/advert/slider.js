$(document).ready(function() {
  $('.slider').slick({
    arrows: true,
    dots: true,
    adaptiveHeight: false,
    slidesToShow: 1,
    slidesToScroll: 1,
    speed: 250,
    initialSlide: 0,
    infinite: true,
    fade: true,
    cssEase: 'linear',
    centerPadding: '150px'
  });
});
