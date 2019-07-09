FROM sidereus/oms:legacy

MAINTAINER Daniele Dalla Torre <dallatorre.daniele@gmail.com>, Francesco Serafin <francesco.serafin.3@gmail.com>

COPY build/libs/jswmm-*-all* /root/.oms/$OMS_VERSION/

ENTRYPOINT ["/entrypoint.sh"]
