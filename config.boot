firewall {
    all-ping enable
    broadcast-ping disable
    group {
        network-group PRIVATE_NETS {
            network 192.168.0.0/16
            network 172.16.0.0/12
            network 10.0.0.0/8
        }
    }
    ipv6-receive-redirects disable
    ipv6-src-route disable
    ip-src-route disable
    log-martians disable
    modify balance {
        rule 10 {
            action modify
            description "do NOT load balance lan to lan"
            destination {
                group {
                    network-group PRIVATE_NETS
                }
            }
            modify {
                table main
            }
        }
        rule 30 {
            action modify
            description "do NOT load balance destination public address"
            destination {
                group {
                    address-group ADDRv4_eth1
                }
            }
            modify {
                table main
            }
        }
        rule 40 {
            action modify
            description "do NOT load balance destination public address"
            destination {
                group {
                    address-group ADDRv4_eth2
                }
            }
            modify {
                table main
            }
        }
        rule 110 {
            action modify
            modify {
                lb-group G
            }
        }
    }
    name WAN_IN {
        default-action accept
        description "WAN to internal"
        rule 10 {
            action accept
            description "Allow established/related"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
    }
    name WAN_LOCAL {
        default-action drop
        description "WAN to router"
        rule 10 {
            action accept
            description "Allow established/related"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
    }
    receive-redirects disable
    send-redirects enable
    source-validation disable
    syn-cookies enable
}
interfaces {
    ethernet eth0 {
        address 192.168.0.1/24
        description Local
        duplex auto
        firewall {
            in {
                modify balance
            }
        }
        speed auto
    }
    ethernet eth1 {
        address dhcp
        description GOOGLE
        duplex auto
        firewall {
            in {
                name WAN_IN
            }
            local {
                name WAN_LOCAL
            }
        }
        speed auto
    }
    ethernet eth2 {
        address dhcp
        description ATT
        duplex auto
        firewall {
            in {
                name WAN_IN
            }
            local {
                name WAN_LOCAL
            }
        }
        speed auto
    }
    loopback lo {
    }
}
load-balance {
    group G {
        interface eth1 {
        }
        interface eth2 {
        }
        lb-local enable
        lb-local-metric-change disable
    }
}
service {
    dhcp-server {
        disabled false
        hostfile-update disable
        shared-network-name LAN {
            authoritative enable
            subnet 192.168.0.0/24 {
                default-router 192.168.0.1
                dns-server 192.168.0.1
                lease 86400
                start 192.168.0.38 {
                    stop 192.168.0.243
                }
            }
        }
        use-dnsmasq disable
    }
    dns {
        forwarding {
            cache-size 150
            listen-on eth0
        }
    }
    gui {
        http-port 80
        https-port 443
        older-ciphers enable
    }
    nat {
        rule 5002 {
            description "masquerade for WAN"
            outbound-interface eth1
            type masquerade
        }
        rule 5004 {
            description "masquerade for WAN 2"
            outbound-interface eth2
            type masquerade
        }
    }
    ssh {
        port 22
        protocol-version v2
    }
}
system {
    conntrack {
        expect-table-size 4096
        hash-size 4096
        table-size 32768
        tcp {
            half-open-connections 512
            loose enable
            max-retrans 3
        }
    }
    host-name ubnt
    login {
        user ubnt {
            authentication {
                encrypted-password "REDACTED"
            }
            level admin
        }
    }
    ntp {
        server 0.ubnt.pool.ntp.org {
        }
        server 1.ubnt.pool.ntp.org {
        }
        server 2.ubnt.pool.ntp.org {
        }
        server 3.ubnt.pool.ntp.org {
        }
    }
    syslog {
        global {
            facility all {
                level notice
            }
            facility protocols {
                level debug
            }
        }
    }
    time-zone UTC
}


/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "config-management@1:conntrack@1:cron@1:dhcp-relay@1:dhcp-server@4:firewall@5:ipsec@5:nat@3:qos@1:quagga@2:system@4:ubnt-pptp@1:ubnt-unms@1:ubnt-util@1:vrrp@1:webgui@1:webproxy@1:zone-policy@1" === */
/* Release version: v1.9.7+hotfix.4.5024004.171005.0403 */
